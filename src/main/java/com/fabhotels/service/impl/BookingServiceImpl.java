package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.entity.Pricing;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.exception.BookingNotFoundException;
import com.fabhotels.exception.GuestCapacityExceededException;
import com.fabhotels.exception.InvalidBookingDateException;
import com.fabhotels.exception.RoomNotAvailableException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.PricingRepository;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log =
            LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final PricingRepository pricingRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            RoomAvailabilityRepository roomAvailabilityRepository,
            PricingRepository pricingRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
        this.pricingRepository = pricingRepository;
    }

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    @Override
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        log.info(
                "Creating booking: roomId={}, checkIn={}, checkOut={}, guests={}",
                request != null ? request.getRoomId() : null,
                request != null ? request.getCheckIn() : null,
                request != null ? request.getCheckOut() : null,
                request != null ? request.getNumberOfGuests() : null
        );

        // 1. Validate request
        validateRequest(request);

        // 2. Get authenticated customer
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated customer is required"
            );
        }

        String authenticatedEmail =
                authentication.getName();

        // 3. Find room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() ->
                        new RoomNotFoundException(request.getRoomId()));

        // 4. Validate room status
        validateRoomStatus(room);

        // 5. Validate guest capacity
        validateGuestCapacity(
                room,
                request.getNumberOfGuests()
        );

        // 6. Check existing confirmed booking
        validateExistingBooking(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 7. Check room availability for every date
        validateRoomAvailability(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 8. Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(
                room,
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 9. Create booking
        Booking booking = new Booking();

        booking.setRoom(room);

        // Guest name can come from the request.
        booking.setGuestName(request.getGuestName());

        // IMPORTANT:
        // Never trust guestEmail from the client.
        // Use the email from the authenticated JWT.
        booking.setGuestEmail(authenticatedEmail);

        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);

        // 10. Save booking
        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking created successfully: bookingId={}, roomId={}, totalAmount={}",
                savedBooking.getId(),
                room.getId(),
                totalAmount
        );

        // 11. Mark room availability as BOOKED
        updateRoomAvailability(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 12. Return response
        return mapToResponse(savedBooking);
    }

    // =========================================================
    // GET BOOKING BY ID
    // =========================================================

    @Override
    @PreAuthorize(
            "hasRole('HOTEL_ADMIN') or @bookingAuthorization.isOwner(#bookingId)"
    )
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(bookingId));

        return mapToResponse(booking);
    }

    // =========================================================
    // GET BOOKINGS BY ROOM
    // =========================================================

    @Override
    @PreAuthorize("hasRole('HOTEL_ADMIN')")
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByRoom(Long roomId) {

        if (!roomRepository.existsById(roomId)) {

            throw new RoomNotFoundException(roomId);
        }

        return bookingRepository
                .findByRoomIdOrderByCheckInAsc(roomId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // REQUEST VALIDATION
    // =========================================================

    private void validateRequest(
            CreateBookingRequest request
    ) {

        if (request == null) {

            throw new InvalidBookingDateException(
                    "Booking request cannot be null"
            );
        }

        if (request.getRoomId() == null) {

            throw new RoomNotAvailableException(
                    "Room id cannot be null"
            );
        }

        if (request.getGuestName() == null
                || request.getGuestName().trim().isEmpty()) {

            throw new InvalidBookingDateException(
                    "Guest name cannot be empty"
            );
        }

        /*
         * guestEmail is intentionally NOT required anymore.
         *
         * It is obtained from the authenticated JWT instead.
         */

        if (request.getCheckIn() == null) {

            throw new InvalidBookingDateException(
                    "Check-in date cannot be null"
            );
        }

        if (request.getCheckOut() == null) {

            throw new InvalidBookingDateException(
                    "Check-out date cannot be null"
            );
        }

        if (!request.getCheckOut()
                .isAfter(request.getCheckIn())) {

            throw new InvalidBookingDateException(
                    "Check-out date must be after check-in date"
            );
        }

        if (request.getNumberOfGuests() == null
                || request.getNumberOfGuests() <= 0) {

            throw new GuestCapacityExceededException(
                    "Number of guests must be greater than 0"
            );
        }
    }

    // =========================================================
    // ROOM STATUS VALIDATION
    // =========================================================

    private void validateRoomStatus(Room room) {

        if (room.getStatus() != RoomStatus.AVAILABLE) {

            throw new RoomNotAvailableException(
                    "Room is not available for booking"
            );
        }
    }

    // =========================================================
    // GUEST CAPACITY VALIDATION
    // =========================================================

    private void validateGuestCapacity(
            Room room,
            Integer numberOfGuests
    ) {

        if (numberOfGuests > room.getCapacity()) {

            throw new GuestCapacityExceededException(
                    "Number of guests exceeds room capacity of "
                            + room.getCapacity()
            );
        }
    }

    // =========================================================
    // EXISTING BOOKING VALIDATION
    // =========================================================

    private void validateExistingBooking(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        boolean overlappingBooking =
                bookingRepository
                        .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                                roomId,
                                BookingStatus.CONFIRMED,
                                checkOut,
                                checkIn
                        );

        if (overlappingBooking) {

            throw new RoomNotAvailableException(
                    "Room is already booked for the requested dates"
            );
        }
    }

    // =========================================================
    // ROOM AVAILABILITY VALIDATION
    // =========================================================

    private void validateRoomAvailability(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        List<RoomAvailability> records =
                roomAvailabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                roomId,
                                checkIn,
                                checkOut
                        );

        long requestedNights =
                java.time.temporal.ChronoUnit.DAYS.between(
                        checkIn,
                        checkOut
                );

        /*
         * Every requested night must have an availability record.
         */
        if (records.size() != requestedNights) {
            throw new RoomNotAvailableException(
                    "Room availability is missing for the requested dates"
            );
        }

        boolean unavailable =
                records.stream()
                        .anyMatch(record ->
                                record.getStatus()
                                        != AvailabilityStatus.AVAILABLE
                        );

        if (unavailable) {
            throw new RoomNotAvailableException(
                    "Room is not available for the requested dates"
            );
        }
    }

    // =========================================================
    // UPDATE ROOM AVAILABILITY
    // =========================================================

    private void updateRoomAvailability(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        List<RoomAvailability> records =
                roomAvailabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                roomId,
                                checkIn,
                                checkOut
                        );

        long requestedNights =
                java.time.temporal.ChronoUnit.DAYS.between(
                        checkIn,
                        checkOut
                );

        if (records.size() != requestedNights) {
            throw new RoomNotAvailableException(
                    "Room availability is missing for the requested dates"
            );
        }

        records.forEach(record ->
                record.setStatus(AvailabilityStatus.BOOKED)
        );

        roomAvailabilityRepository.saveAll(records);
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    private BookingResponse mapToResponse(
            Booking booking
    ) {

        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getNumberOfGuests(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }

    // =========================================================
    // CALCULATE TOTAL AMOUNT
    // =========================================================

    private BigDecimal calculateTotalAmount(
            Room room,
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        List<Pricing> pricingRecords =
                pricingRepository
                        .findByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThanOrderByStartDateAsc(
                                room.getId(),
                                checkOut,
                                checkIn
                        );

        BigDecimal totalAmount = BigDecimal.ZERO;

        LocalDate date = checkIn;

        while (date.isBefore(checkOut)) {

            final LocalDate currentDate = date;

            BigDecimal nightlyPrice = pricingRecords.stream()
                    .filter(pricing ->
                            !currentDate.isBefore(pricing.getStartDate())
                                    && currentDate.isBefore(pricing.getEndDate())
                    )
                    .map(Pricing::getPricePerNight)
                    .findFirst()
                    .orElse(room.getPricePerNight());

            totalAmount = totalAmount.add(nightlyPrice);

            date = date.plusDays(1);
        }

        return totalAmount;
    }
}