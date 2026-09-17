package com.fabhotels.integration;

import com.fabhotels.dto.request.CancelBookingRequest;
import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.request.CreatePaymentRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.dto.response.CancellationResponse;
import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.PaymentMethod;
import com.fabhotels.enums.PaymentStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.BookingService;
import com.fabhotels.service.CancellationService;
import com.fabhotels.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingPaymentCancellationIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CancellationService cancellationService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomAvailabilityRepository availabilityRepository;

    @Test
    void bookingPaymentAndCancellation_shouldCompleteFullFlow() {

        Hotel hotel =
                new Hotel(
                        null,
                        "Integration Hotel",
                        "d",
                        "address",
                        "Bengaluru",
                        "Karnataka",
                        "India",
                        "560001",
                        true,
                        null
                );

        hotel = hotelRepository.save(hotel);

        Room room =
                new Room(
                        null,
                        hotel,
                        "101",
                        RoomType.DOUBLE,
                        new BigDecimal("2500.00"),
                        2,
                        RoomStatus.AVAILABLE,
                        null
                );

        room = roomRepository.save(room);

        LocalDate checkIn =
                LocalDate.of(2035, 1, 10);

        LocalDate checkOut =
                LocalDate.of(2035, 1, 12);

        addAvailability(
                room,
                checkIn
        );

        addAvailability(
                room,
                checkIn.plusDays(1)
        );

        CreateBookingRequest bookingRequest =
                new CreateBookingRequest();

        bookingRequest.setRoomId(
                room.getId()
        );

        bookingRequest.setGuestName(
                "Integration Guest"
        );

        bookingRequest.setGuestEmail(
                "integration@test.com"
        );

        bookingRequest.setCheckIn(
                checkIn
        );

        bookingRequest.setCheckOut(
                checkOut
        );

        bookingRequest.setNumberOfGuests(2);

        BookingResponse booking =
                bookingService.createBooking(
                        bookingRequest
                );

        assertThat(
                booking.getStatus()
        ).isEqualTo(
                BookingStatus.CONFIRMED
        );

        assertThat(
                booking.getTotalAmount()
        ).isEqualByComparingTo(
                "5000.00"
        );

        CreatePaymentRequest paymentRequest =
                new CreatePaymentRequest();

        paymentRequest.setBookingId(
                booking.getId()
        );

        paymentRequest.setAmount(
                booking.getTotalAmount()
        );

        paymentRequest.setPaymentMethod(
                PaymentMethod.UPI
        );

        PaymentResponse payment =
                paymentService.createPayment(
                        paymentRequest
                );

        assertThat(
                payment.getStatus()
        ).isEqualTo(
                PaymentStatus.SUCCESS
        );

        assertThat(
                payment.getAmount()
        ).isEqualByComparingTo(
                "5000.00"
        );

        assertThat(
                payment.getTransactionId()
        ).startsWith("TXN-");

        CancelBookingRequest cancelRequest =
                new CancelBookingRequest();

        cancelRequest.setReason(
                "Integration test cancellation"
        );

        CancellationResponse cancellation =
                cancellationService.cancelBooking(
                        booking.getId(),
                        cancelRequest
                );

        assertThat(
                cancellation.getStatus()
        ).isEqualTo(
                BookingStatus.CANCELLED
        );

        assertThat(
                cancellation.getRefundAmount()
        ).isEqualByComparingTo(
                "5000.00"
        );

        assertThat(
                cancellation.getReason()
        ).isEqualTo(
                "Integration test cancellation"
        );

        assertThat(
                availabilityRepository
                        .findByRoomIdAndDate(
                                room.getId(),
                                checkIn
                        )
                        .orElseThrow()
                        .getStatus()
        ).isEqualTo(
                AvailabilityStatus.AVAILABLE
        );

        assertThat(
                availabilityRepository
                        .findByRoomIdAndDate(
                                room.getId(),
                                checkIn.plusDays(1)
                        )
                        .orElseThrow()
                        .getStatus()
        ).isEqualTo(
                AvailabilityStatus.AVAILABLE
        );
    }

    private void addAvailability(
            Room room,
            LocalDate date
    ) {

        RoomAvailability availability =
                new RoomAvailability();

        availability.setRoom(room);
        availability.setDate(date);
        availability.setStatus(
                AvailabilityStatus.AVAILABLE
        );

        availabilityRepository.save(
                availability
        );
    }
}