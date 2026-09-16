package com.fabhotels.service;

import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.exception.BookingNotFoundException;
import com.fabhotels.exception.GuestCapacityExceededException;
import com.fabhotels.exception.InvalidBookingDateException;
import com.fabhotels.exception.RoomNotAvailableException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Room room;
    private CreateBookingRequest request;

    @BeforeEach
    void setUp() {

        room = new Room();

        room.setId(1L);
        room.setRoomNumber("101");
        room.setPricePerNight(new BigDecimal("2000.00"));
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);

        request = new CreateBookingRequest();

        request.setRoomId(1L);
        request.setGuestName("Test User");
        request.setGuestEmail("test@example.com");
        request.setCheckIn(LocalDate.of(2026, 10, 10));
        request.setCheckOut(LocalDate.of(2026, 10, 13));
        request.setNumberOfGuests(2);
    }

    // =========================================================
    // SUCCESSFUL BOOKING
    // =========================================================

    @Test
    void createBooking_shouldCreateBookingSuccessfully() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(1L),
                        eq(BookingStatus.CONFIRMED),
                        eq(LocalDate.of(2026, 10, 13)),
                        eq(LocalDate.of(2026, 10, 10))))
                .thenReturn(false);

        RoomAvailability availability10 =
                createAvailability(
                        LocalDate.of(2026, 10, 10),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability availability11 =
                createAvailability(
                        LocalDate.of(2026, 10, 11),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability availability12 =
                createAvailability(
                        LocalDate.of(2026, 10, 12),
                        AvailabilityStatus.AVAILABLE
                );

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L, LocalDate.of(2026, 10, 10)))
                .thenReturn(Optional.of(availability10));

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L, LocalDate.of(2026, 10, 11)))
                .thenReturn(Optional.of(availability11));

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L, LocalDate.of(2026, 10, 12)))
                .thenReturn(Optional.of(availability12));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {

                    Booking booking = invocation.getArgument(0);

                    booking.setId(501L);
                    booking.setCreatedAt(
                            LocalDateTime.of(
                                    2026,
                                    9,
                                    16,
                                    12,
                                    30
                            )
                    );

                    return booking;
                });

        BookingResponse response =
                bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(501L, response.getId());
        assertEquals(1L, response.getRoomId());
        assertEquals("Test User", response.getGuestName());
        assertEquals("test@example.com", response.getGuestEmail());

        assertEquals(
                LocalDate.of(2026, 10, 10),
                response.getCheckIn()
        );

        assertEquals(
                LocalDate.of(2026, 10, 13),
                response.getCheckOut()
        );

        assertEquals(2, response.getNumberOfGuests());

        assertEquals(
                new BigDecimal("6000.00"),
                response.getTotalAmount()
        );

        assertEquals(
                BookingStatus.CONFIRMED,
                response.getStatus()
        );

        verify(bookingRepository, times(1))
                .save(any(Booking.class));

        verify(roomAvailabilityRepository, times(3))
                .save(any(RoomAvailability.class));

        assertEquals(
                AvailabilityStatus.BOOKED,
                availability10.getStatus()
        );

        assertEquals(
                AvailabilityStatus.BOOKED,
                availability11.getStatus()
        );

        assertEquals(
                AvailabilityStatus.BOOKED,
                availability12.getStatus()
        );
    }

    // =========================================================
    // ROOM VALIDATION
    // =========================================================

    @Test
    void createBooking_whenRoomDoesNotExist_shouldThrowException() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RoomNotFoundException.class,
                () -> bookingService.createBooking(request)
        );

        verify(roomRepository, times(1))
                .findById(1L);

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(roomAvailabilityRepository);
    }

    @Test
    void createBooking_whenRoomIsMaintenance_shouldThrowException() {

        room.setStatus(RoomStatus.MAINTENANCE);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(roomAvailabilityRepository);
    }

    @Test
    void createBooking_whenRoomIsInactive_shouldThrowException() {

        room.setStatus(RoomStatus.INACTIVE);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(roomAvailabilityRepository);
    }

    // =========================================================
    // DATE VALIDATION
    // =========================================================

    @Test
    void createBooking_whenCheckInIsNull_shouldThrowException() {

        request.setCheckIn(null);

        assertThrows(
                InvalidBookingDateException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_whenCheckOutIsNull_shouldThrowException() {

        request.setCheckOut(null);

        assertThrows(
                InvalidBookingDateException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_whenCheckOutEqualsCheckIn_shouldThrowException() {

        request.setCheckOut(
                LocalDate.of(2026, 10, 10)
        );

        assertThrows(
                InvalidBookingDateException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_whenCheckOutBeforeCheckIn_shouldThrowException() {

        request.setCheckOut(
                LocalDate.of(2026, 10, 9)
        );

        assertThrows(
                InvalidBookingDateException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(roomRepository);
    }

    // =========================================================
    // GUEST CAPACITY
    // =========================================================

    @Test
    void createBooking_whenGuestsExceedCapacity_shouldThrowException() {

        request.setNumberOfGuests(3);

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        assertThrows(
                GuestCapacityExceededException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(roomAvailabilityRepository);
    }

    @Test
    void createBooking_whenGuestsAreZero_shouldThrowException() {

        request.setNumberOfGuests(0);

        assertThrows(
                GuestCapacityExceededException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_whenGuestsAreNegative_shouldThrowException() {

        request.setNumberOfGuests(-1);

        assertThrows(
                GuestCapacityExceededException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(roomRepository);
    }

    // =========================================================
    // EXISTING BOOKING / OVERLAP
    // =========================================================

    @Test
    void createBooking_whenDatesOverlap_shouldThrowException() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(1L),
                        eq(BookingStatus.CONFIRMED),
                        eq(LocalDate.of(2026, 10, 13)),
                        eq(LocalDate.of(2026, 10, 10))))
                .thenReturn(true);

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(
                bookingRepository,
                times(1)
        ).existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                eq(1L),
                eq(BookingStatus.CONFIRMED),
                eq(LocalDate.of(2026, 10, 13)),
                eq(LocalDate.of(2026, 10, 10))
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));

        verifyNoInteractions(roomAvailabilityRepository);
    }

    @Test
    void createBooking_whenAdjacentBookingExists_shouldContinue() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        anyLong(),
                        eq(BookingStatus.CONFIRMED),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(false);

        RoomAvailability availability13 =
                createAvailability(
                        LocalDate.of(2026, 10, 13),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability availability14 =
                createAvailability(
                        LocalDate.of(2026, 10, 14),
                        AvailabilityStatus.AVAILABLE
                );

        RoomAvailability availability15 =
                createAvailability(
                        LocalDate.of(2026, 10, 15),
                        AvailabilityStatus.AVAILABLE
                );

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L, LocalDate.of(2026, 10, 10)))
                .thenReturn(Optional.of(
                        createAvailability(
                                LocalDate.of(2026, 10, 10),
                                AvailabilityStatus.AVAILABLE
                        )
                ));

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L, LocalDate.of(2026, 10, 11)))
                .thenReturn(Optional.of(
                        createAvailability(
                                LocalDate.of(2026, 10, 11),
                                AvailabilityStatus.AVAILABLE
                        )
                ));

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L, LocalDate.of(2026, 10, 12)))
                .thenReturn(Optional.of(
                        createAvailability(
                                LocalDate.of(2026, 10, 12),
                                AvailabilityStatus.AVAILABLE
                        )
                ));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {

                    Booking booking = invocation.getArgument(0);
                    booking.setId(501L);
                    booking.setCreatedAt(LocalDateTime.now());

                    return booking;
                });

        BookingResponse response =
                bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(
                new BigDecimal("6000.00"),
                response.getTotalAmount()
        );

        assertNotNull(availability13);
        assertNotNull(availability14);
        assertNotNull(availability15);
    }

    // =========================================================
    // ROOM AVAILABILITY
    // =========================================================

    @Test
    void createBooking_whenAvailabilityIsBooked_shouldThrowException() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        anyLong(),
                        eq(BookingStatus.CONFIRMED),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(false);

        RoomAvailability availability =
                createAvailability(
                        LocalDate.of(2026, 10, 10),
                        AvailabilityStatus.BOOKED
                );

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L,
                LocalDate.of(2026, 10, 10)))
                .thenReturn(Optional.of(availability));

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_whenAvailabilityIsBlocked_shouldThrowException() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        anyLong(),
                        eq(BookingStatus.CONFIRMED),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(false);

        RoomAvailability availability =
                createAvailability(
                        LocalDate.of(2026, 10, 10),
                        AvailabilityStatus.BLOCKED
                );

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L,
                LocalDate.of(2026, 10, 10)))
                .thenReturn(Optional.of(availability));

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_whenAvailabilityIsMissing_shouldThrowException() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        anyLong(),
                        eq(BookingStatus.CONFIRMED),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(false);

        when(roomAvailabilityRepository.findByRoomIdAndDate(
                1L,
                LocalDate.of(2026, 10, 10)))
                .thenReturn(Optional.empty());

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    // =========================================================
    // AMOUNT CALCULATION
    // =========================================================

    @Test
    void createBooking_shouldCalculateCorrectTotalAmount() {

        request.setCheckIn(
                LocalDate.of(2026, 10, 10)
        );

        request.setCheckOut(
                LocalDate.of(2026, 10, 15)
        );

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        anyLong(),
                        eq(BookingStatus.CONFIRMED),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(false);

        for (int day = 10; day <= 14; day++) {

            when(roomAvailabilityRepository.findByRoomIdAndDate(
                    1L,
                    LocalDate.of(2026, 10, day)))
                    .thenReturn(
                            Optional.of(
                                    createAvailability(
                                            LocalDate.of(
                                                    2026,
                                                    10,
                                                    day
                                            ),
                                            AvailabilityStatus.AVAILABLE
                                    )
                            )
                    );
        }

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {

                    Booking booking = invocation.getArgument(0);

                    booking.setId(502L);
                    booking.setCreatedAt(LocalDateTime.now());

                    return booking;
                });

        BookingResponse response =
                bookingService.createBooking(request);

        // 5 nights × ₹2000
        assertEquals(
                new BigDecimal("10000.00"),
                response.getTotalAmount()
        );
    }

    // =========================================================
    // GET BOOKING
    // =========================================================

    @Test
    void getBookingById_shouldReturnBooking() {

        Booking booking = createBooking();

        when(bookingRepository.findById(501L))
                .thenReturn(Optional.of(booking));

        BookingResponse response =
                bookingService.getBookingById(501L);

        assertNotNull(response);
        assertEquals(501L, response.getId());
        assertEquals("Test User", response.getGuestName());
        assertEquals(1L, response.getRoomId());

        verify(bookingRepository, times(1))
                .findById(501L);
    }

    @Test
    void getBookingById_whenBookingDoesNotExist_shouldThrowException() {

        when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(999L)
        );

        verify(bookingRepository, times(1))
                .findById(999L);
    }

    // =========================================================
    // GET ROOM BOOKINGS
    // =========================================================

    @Test
    void getBookingsByRoom_shouldReturnBookings() {

        Booking booking = createBooking();

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        when(bookingRepository.findByRoomIdOrderByCheckInAsc(1L))
                .thenReturn(List.of(booking));

        List<BookingResponse> responses =
                bookingService.getBookingsByRoom(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(501L, responses.get(0).getId());
        assertEquals("Test User", responses.get(0).getGuestName());

        verify(roomRepository, times(1))
                .existsById(1L);

        verify(bookingRepository, times(1))
                .findByRoomIdOrderByCheckInAsc(1L);
    }

    @Test
    void getBookingsByRoom_whenRoomDoesNotExist_shouldThrowException() {

        when(roomRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                RoomNotFoundException.class,
                () -> bookingService.getBookingsByRoom(999L)
        );

        verify(roomRepository, times(1))
                .existsById(999L);

        verify(bookingRepository, never())
                .findByRoomIdOrderByCheckInAsc(anyLong());
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private RoomAvailability createAvailability(
            LocalDate date,
            AvailabilityStatus status) {

        RoomAvailability availability =
                new RoomAvailability();

        availability.setRoom(room);
        availability.setDate(date);
        availability.setStatus(status);

        return availability;
    }

    private Booking createBooking() {

        Booking booking = new Booking();

        booking.setId(501L);
        booking.setRoom(room);
        booking.setGuestName("Test User");
        booking.setGuestEmail("test@example.com");

        booking.setCheckIn(
                LocalDate.of(2026, 10, 10)
        );

        booking.setCheckOut(
                LocalDate.of(2026, 10, 13)
        );

        booking.setNumberOfGuests(2);
        booking.setTotalAmount(
                new BigDecimal("6000.00")
        );

        booking.setStatus(
                BookingStatus.CONFIRMED
        );

        booking.setCreatedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        12,
                        30
                )
        );

        return booking;
    }
}