package com.fabhotels.service;

import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Pricing;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import com.fabhotels.exception.BookingNotFoundException;
import com.fabhotels.exception.RoomNotAvailableException;
import com.fabhotels.exception.RoomNotFoundException;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.PricingRepository;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Mock
    private PricingRepository pricingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Room room;
    private Hotel hotel;

    private final LocalDate checkIn =
            LocalDate.of(2035, 1, 10);

    private final LocalDate checkOut =
            LocalDate.of(2035, 1, 13);

    @BeforeEach
    void setUpSecurityContext() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "rinkal@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @BeforeEach
    void setUp() {

        hotel = new Hotel();

        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setCity("Bangalore");
        hotel.setState("Karnataka");
        hotel.setAddress("Test Address");
        hotel.setCountry("India");
        hotel.setPincode("560001");
        hotel.setActive(true);

        room = new Room();

        room.setId(10L);
        room.setHotel(hotel);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DOUBLE);
        room.setPricePerNight(new BigDecimal("2000.00"));
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);
    }

    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void createBooking_shouldCreateBookingSuccessfully() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(false);

        mockAvailableAvailability();

        when(pricingRepository
                .findByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThanOrderByStartDateAsc(
                        eq(10L),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(List.of());

        Booking savedBooking = new Booking();

        savedBooking.setId(101L);
        savedBooking.setRoom(room);
        savedBooking.setGuestName("Rinkal");
        savedBooking.setGuestEmail("rinkal@test.com");
        savedBooking.setCheckIn(checkIn);
        savedBooking.setCheckOut(checkOut);
        savedBooking.setNumberOfGuests(2);
        savedBooking.setTotalAmount(new BigDecimal("6000.00"));
        savedBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        BookingResponse response =
                bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals(
                new BigDecimal("6000.00"),
                response.getTotalAmount()
        );

        verify(bookingRepository)
                .save(any(Booking.class));
    }

    @Test
    void createBooking_shouldCalculateCorrectTotalAmount() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(false);

        mockAvailableAvailability();

        when(pricingRepository
                .findByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThanOrderByStartDateAsc(
                        eq(10L),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(List.of());

        Booking savedBooking = new Booking();

        savedBooking.setId(102L);
        savedBooking.setRoom(room);
        savedBooking.setGuestName("Rinkal");
        savedBooking.setGuestEmail("rinkal@test.com");
        savedBooking.setCheckIn(checkIn);
        savedBooking.setCheckOut(checkOut);
        savedBooking.setNumberOfGuests(2);
        savedBooking.setTotalAmount(new BigDecimal("6000.00"));
        savedBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        BookingResponse response =
                bookingService.createBooking(request);

        assertEquals(
                new BigDecimal("6000.00"),
                response.getTotalAmount()
        );

        verify(bookingRepository)
                .save(argThat(booking ->
                        booking.getTotalAmount()
                                .compareTo(new BigDecimal("6000.00")) == 0
                ));
    }

    @Test
    void createBooking_whenAdjacentBookingExists_shouldContinue() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        /*
         * Existing booking ends exactly when the new booking starts.
         * It must NOT be treated as an overlap.
         */
        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(false);

        mockAvailableAvailability();

        when(pricingRepository
                .findByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThanOrderByStartDateAsc(
                        eq(10L),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(List.of());

        Booking savedBooking =
                createSavedBooking(103L);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        BookingResponse response =
                bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(103L, response.getId());
    }

    @Test
    void createBooking_whenAvailabilityIsBooked_shouldThrowException() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(false);

        List<RoomAvailability> records =
                new ArrayList<>();

        records.add(
                createAvailability(
                        checkIn,
                        AvailabilityStatus.BOOKED
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(1),
                        AvailabilityStatus.AVAILABLE
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(2),
                        AvailabilityStatus.AVAILABLE
                )
        );

        when(roomAvailabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        eq(10L),
                        eq(checkIn),
                        eq(checkOut)))
                .thenReturn(records);

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_whenAvailabilityIsBlocked_shouldThrowException() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(false);

        List<RoomAvailability> records =
                new ArrayList<>();

        records.add(
                createAvailability(
                        checkIn,
                        AvailabilityStatus.BLOCKED
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(1),
                        AvailabilityStatus.AVAILABLE
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(2),
                        AvailabilityStatus.AVAILABLE
                )
        );

        when(roomAvailabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        eq(10L),
                        eq(checkIn),
                        eq(checkOut)))
                .thenReturn(records);

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_whenAvailabilityIsMissing_shouldThrowException() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(false);

        List<RoomAvailability> records =
                new ArrayList<>();

        /*
         * Only two nights returned instead of three.
         */
        records.add(
                createAvailability(
                        checkIn,
                        AvailabilityStatus.AVAILABLE
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(1),
                        AvailabilityStatus.AVAILABLE
                )
        );

        when(roomAvailabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        eq(10L),
                        eq(checkIn),
                        eq(checkOut)))
                .thenReturn(records);

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_whenRoomNotFound_shouldThrowException() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                RoomNotFoundException.class,
                () -> bookingService.createBooking(request)
        );
    }

    @Test
    void createBooking_whenBookingOverlaps_shouldThrowException() {

        CreateBookingRequest request = createRequest();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository
                .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                        eq(10L),
                        eq(BookingStatus.CONFIRMED),
                        eq(checkOut),
                        eq(checkIn)))
                .thenReturn(true);

        assertThrows(
                RoomNotAvailableException.class,
                () -> bookingService.createBooking(request)
        );

        verify(roomAvailabilityRepository, never())
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        anyLong(),
                        any(LocalDate.class),
                        any(LocalDate.class)
                );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    private CreateBookingRequest createRequest() {

        CreateBookingRequest request =
                new CreateBookingRequest();

        request.setRoomId(10L);
        request.setGuestName("Rinkal");
        request.setGuestEmail("rinkal@test.com");
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        request.setNumberOfGuests(2);

        return request;
    }

    private void mockAvailableAvailability() {

        List<RoomAvailability> records =
                new ArrayList<>();

        records.add(
                createAvailability(
                        checkIn,
                        AvailabilityStatus.AVAILABLE
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(1),
                        AvailabilityStatus.AVAILABLE
                )
        );

        records.add(
                createAvailability(
                        checkIn.plusDays(2),
                        AvailabilityStatus.AVAILABLE
                )
        );

        when(roomAvailabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        eq(10L),
                        eq(checkIn),
                        eq(checkOut)))
                .thenReturn(records);
    }

    private RoomAvailability createAvailability(
            LocalDate date,
            AvailabilityStatus status
    ) {

        RoomAvailability availability =
                new RoomAvailability();

        availability.setId(date.toEpochDay());
        availability.setRoom(room);
        availability.setDate(date);
        availability.setStatus(status);

        return availability;
    }

    private Booking createSavedBooking(Long id) {

        Booking booking = new Booking();

        booking.setId(id);
        booking.setRoom(room);
        booking.setGuestName("Rinkal");
        booking.setGuestEmail("rinkal@test.com");
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNumberOfGuests(2);
        booking.setTotalAmount(new BigDecimal("6000.00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        return booking;
    }
}