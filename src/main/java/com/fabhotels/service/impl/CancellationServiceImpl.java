package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CancelBookingRequest;
import com.fabhotels.dto.response.CancellationResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.entity.Payment;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.PaymentStatus;
import com.fabhotels.exception.BookingAlreadyCancelledException;
import com.fabhotels.exception.BookingNotFoundException;
import com.fabhotels.exception.InvalidCancellationException;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.PaymentRepository;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.service.CancellationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CancellationServiceImpl implements CancellationService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    public CancellationServiceImpl(
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            RoomAvailabilityRepository roomAvailabilityRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
    }

    @Override
    @Transactional
    public CancellationResponse cancelBooking(
            Long bookingId,
            CancelBookingRequest request
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(bookingId));

        // Already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingAlreadyCancelledException(bookingId);
        }

        // Only confirmed bookings can be cancelled
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidCancellationException(
                    "Only confirmed bookings can be cancelled"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        validateCancellationTime(booking, now);

        BigDecimal refundAmount =
                calculateRefundAmount(booking, now);

        // Update booking
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(now);

        if (request != null) {
            booking.setCancellationReason(request.getReason());
        }

        // Release booked room nights
        releaseRoomAvailability(booking);

        // Save cancelled booking
        Booking savedBooking = bookingRepository.save(booking);

        return new CancellationResponse(
                savedBooking.getId(),
                savedBooking.getStatus(),
                savedBooking.getCancelledAt(),
                refundAmount,
                savedBooking.getCancellationReason()
        );
    }

    private void validateCancellationTime(
            Booking booking,
            LocalDateTime now
    ) {

        LocalDate checkIn = booking.getCheckIn();

        LocalDateTime checkInDateTime =
                checkIn.atStartOfDay();

        if (!now.isBefore(checkInDateTime)) {
            throw new InvalidCancellationException(
                    "Booking cannot be cancelled on or after check-in date"
            );
        }
    }

    private BigDecimal calculateRefundAmount(
            Booking booking,
            LocalDateTime now
    ) {

        Payment payment = paymentRepository
                .findByBookingId(booking.getId())
                .orElse(null);

        // No payment = no refund
        if (payment == null ||
                payment.getStatus() != PaymentStatus.SUCCESS) {

            return BigDecimal.ZERO;
        }

        LocalDateTime checkInDateTime =
                booking.getCheckIn().atStartOfDay();

        long hoursUntilCheckIn =
                Duration.between(now, checkInDateTime).toHours();

        BigDecimal paidAmount = payment.getAmount();

        // More than 48 hours
        if (hoursUntilCheckIn > 48) {
            return paidAmount;
        }

        // 24 to 48 hours
        if (hoursUntilCheckIn >= 24) {
            return paidAmount
                    .multiply(new BigDecimal("0.50"));
        }

        // Less than 24 hours
        return BigDecimal.ZERO;
    }

    private void releaseRoomAvailability(Booking booking) {

        Long roomId = booking.getRoom().getId();

        LocalDate currentDate = booking.getCheckIn();
        LocalDate checkOut = booking.getCheckOut();

        while (currentDate.isBefore(checkOut)) {

            final LocalDate dateToRelease = currentDate;

            RoomAvailability availability =
                    roomAvailabilityRepository
                            .findByRoomIdAndDate(
                                    roomId,
                                    dateToRelease
                            )
                            .orElseThrow(() ->
                                    new InvalidCancellationException(
                                            "Room availability record missing for date: "
                                                    + dateToRelease
                                    )
                            );

            /*
             * Only release dates that are actually BOOKED.
             */
            if (availability.getStatus() ==
                    AvailabilityStatus.BOOKED) {

                availability.setStatus(
                        AvailabilityStatus.AVAILABLE
                );

                roomAvailabilityRepository.save(
                        availability
                );

            } else if (availability.getStatus() ==
                    AvailabilityStatus.BLOCKED) {

                throw new InvalidCancellationException(
                        "Cannot release blocked room availability for date: "
                                + dateToRelease
                );
            }

            currentDate = currentDate.plusDays(1);
        }
    }
}