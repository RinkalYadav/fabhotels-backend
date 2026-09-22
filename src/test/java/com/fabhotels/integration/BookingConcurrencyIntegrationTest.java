package com.fabhotels.integration;

import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.entity.Booking;
import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import com.fabhotels.repository.BookingRepository;
import com.fabhotels.repository.HotelRepository;
import com.fabhotels.repository.RoomAvailabilityRepository;
import com.fabhotels.repository.RoomRepository;
import com.fabhotels.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingConcurrencyIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Room room;

    private final LocalDate checkIn =
            LocalDate.of(2035, 1, 10);

    private final LocalDate checkOut =
            LocalDate.of(2035, 1, 13);

    @BeforeEach
    void setUp() {

        bookingRepository.deleteAll();
        roomAvailabilityRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();

        Hotel hotel = new Hotel();

        hotel.setName("Concurrency Test Hotel");
        hotel.setCity("Bangalore");
        hotel.setState("Karnataka");
        hotel.setAddress("Test Address");
        hotel.setCountry("India");
        hotel.setPincode("560001");
        hotel.setActive(true);

        hotel = hotelRepository.save(hotel);

        room = new Room();

        room.setHotel(hotel);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.DOUBLE);
        room.setPricePerNight(new BigDecimal("2000.00"));
        room.setCapacity(2);
        room.setStatus(RoomStatus.AVAILABLE);

        room = roomRepository.save(room);

        List<RoomAvailability> availability =
                new ArrayList<>();

        LocalDate date = checkIn;

        while (date.isBefore(checkOut)) {

            RoomAvailability roomAvailability =
                    new RoomAvailability();

            roomAvailability.setRoom(room);
            roomAvailability.setDate(date);
            roomAvailability.setStatus(
                    AvailabilityStatus.AVAILABLE
            );

            availability.add(roomAvailability);

            date = date.plusDays(1);
        }

        roomAvailabilityRepository.saveAll(availability);
    }

    @Test
    void concurrentBooking_shouldAllowOnlyOneBooking()
            throws Exception {

        CreateBookingRequest requestA =
                createRequest(
                        "Customer A",
                        "customer-a@test.com"
                );

        CreateBookingRequest requestB =
                createRequest(
                        "Customer B",
                        "customer-b@test.com"
                );

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        Future<BookingResult> futureA =
                executor.submit(() -> {

                    startLatch.await();

                    return executeBooking(
                            requestA,
                            "customer-a@test.com"
                    );
                });

        Future<BookingResult> futureB =
                executor.submit(() -> {

                    startLatch.await();

                    return executeBooking(
                            requestB,
                            "customer-b@test.com"
                    );
                });

        startLatch.countDown();

        BookingResult resultA =
                futureA.get();

        BookingResult resultB =
                futureB.get();

        executor.shutdown();

        int successfulBookings = 0;

        if (resultA.success()) {
            successfulBookings++;
        }

        if (resultB.success()) {
            successfulBookings++;
        }

        assertEquals(
                1,
                successfulBookings,
                "Exactly one concurrent booking should succeed"
        );

        assertEquals(
                1,
                bookingRepository.count(),
                "Only one booking should be created"
        );

        List<RoomAvailability> availability =
                roomAvailabilityRepository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                room.getId(),
                                checkIn,
                                checkOut
                        );

        assertEquals(3, availability.size());

        assertTrue(
                availability.stream()
                        .allMatch(roomAvailability ->
                                roomAvailability.getStatus()
                                        == AvailabilityStatus.BOOKED)
        );
    }

    private BookingResult executeBooking(
            CreateBookingRequest request,
            String email
    ) {

        try {

            setAuthentication(email);

            BookingResponse response =
                    bookingService.createBooking(request);

            return new BookingResult(
                    true,
                    response.getId()
            );

        } catch (Exception exception) {

            return new BookingResult(
                    false,
                    null
            );

        } finally {

            SecurityContextHolder.clearContext();
        }
    }

    private void setAuthentication(String email) {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_CUSTOMER"
                                )
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    private CreateBookingRequest createRequest(
            String guestName,
            String guestEmail
    ) {

        CreateBookingRequest request =
                new CreateBookingRequest();

        request.setRoomId(room.getId());
        request.setGuestName(guestName);
        request.setGuestEmail(guestEmail);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);
        request.setNumberOfGuests(2);

        return request;
    }

    private record BookingResult(
            boolean success,
            Long bookingId
    ) {
    }
}