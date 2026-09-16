package com.fabhotels.service.impl;

import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.entity.Booking;
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
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            RoomAvailabilityRepository roomAvailabilityRepository) {

        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.roomAvailabilityRepository = roomAvailabilityRepository;
    }

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    @Override
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        // 1. Validate request
        validateRequest(request);

        // 2. Find room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() ->
                        new RoomNotFoundException(request.getRoomId()));

        // 3. Validate room status
        validateRoomStatus(room);

        // 4. Validate guest capacity
        validateGuestCapacity(
                room,
                request.getNumberOfGuests()
        );

        // 5. Check existing confirmed booking
        validateExistingBooking(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 6. Check room availability for every date
        validateRoomAvailability(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 7. Calculate number of nights
        long numberOfNights = ChronoUnit.DAYS.between(
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 8. Calculate total amount
        BigDecimal totalAmount =
                room.getPricePerNight()
                        .multiply(
                                BigDecimal.valueOf(numberOfNights)
                        );

        // 9. Create booking
        Booking booking = new Booking();

        booking.setRoom(room);
        booking.setGuestName(request.getGuestName());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);

        // 10. Save booking
        Booking savedBooking =
                bookingRepository.save(booking);

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
            CreateBookingRequest request) {

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

        if (request.getGuestEmail() == null
                || request.getGuestEmail().trim().isEmpty()) {

            throw new InvalidBookingDateException(
                    "Guest email cannot be empty"
            );
        }

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
            Integer numberOfGuests) {

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
            LocalDate checkOut) {

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
            LocalDate checkOut) {

        LocalDate date = checkIn;

        while (date.isBefore(checkOut)) {

            // Create a final/effectively-final variable
            // for use inside lambda
            final LocalDate currentDate = date;

            RoomAvailability availability =
                    roomAvailabilityRepository
                            .findByRoomIdAndDate(
                                    roomId,
                                    currentDate
                            )
                            .orElseThrow(() ->
                                    new RoomNotAvailableException(
                                            "Room availability is missing for "
                                                    + currentDate
                                    ));

            if (availability.getStatus()
                    != AvailabilityStatus.AVAILABLE) {

                throw new RoomNotAvailableException(
                        "Room is not available for "
                                + currentDate
                );
            }

            date = date.plusDays(1);
        }
    }

    // =========================================================
    // UPDATE ROOM AVAILABILITY
    // =========================================================

    private void updateRoomAvailability(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut) {

        LocalDate date = checkIn;

        while (date.isBefore(checkOut)) {

            // Final variable for lambda
            final LocalDate currentDate = date;

            RoomAvailability availability =
                    roomAvailabilityRepository
                            .findByRoomIdAndDate(
                                    roomId,
                                    currentDate
                            )
                            .orElseThrow(() ->
                                    new RoomNotAvailableException(
                                            "Room availability is missing for "
                                                    + currentDate
                                    ));

            availability.setStatus(
                    AvailabilityStatus.BOOKED
            );

            roomAvailabilityRepository.save(
                    availability
            );

            date = date.plusDays(1);
        }
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    private BookingResponse mapToResponse(
            Booking booking) {

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
}