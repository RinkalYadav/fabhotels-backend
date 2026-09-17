package com.fabhotels.repository;

import com.fabhotels.entity.Booking;
import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Room;
import com.fabhotels.enums.BookingStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Room room() {

        Hotel hotel =
                new Hotel(
                        null,
                        "Hotel",
                        "desc",
                        "addr",
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
                        new BigDecimal("2000"),
                        2,
                        RoomStatus.AVAILABLE,
                        null
                );

        return roomRepository.save(room);
    }

    private Booking booking(
            Room room,
            LocalDate checkIn,
            LocalDate checkOut,
            BookingStatus status
    ) {

        Booking booking = new Booking();

        booking.setRoom(room);
        booking.setGuestName("Guest");
        booking.setGuestEmail("guest@test.com");
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNumberOfGuests(1);
        booking.setTotalAmount(
                new BigDecimal("4000")
        );
        booking.setStatus(status);

        return bookingRepository.save(booking);
    }

    @Test
    void overlapQuery_shouldReturnTrueForOverlappingConfirmedBooking() {

        Room room = room();

        booking(
                room,
                LocalDate.of(2030, 1, 10),
                LocalDate.of(2030, 1, 15),
                BookingStatus.CONFIRMED
        );

        assertThat(
                bookingRepository
                        .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                                room.getId(),
                                BookingStatus.CONFIRMED,
                                LocalDate.of(2030, 1, 12),
                                LocalDate.of(2030, 1, 11)
                        )
        ).isTrue();
    }

    @Test
    void overlapQuery_shouldReturnFalseForAdjacentDates() {

        Room room = room();

        booking(
                room,
                LocalDate.of(2030, 1, 10),
                LocalDate.of(2030, 1, 15),
                BookingStatus.CONFIRMED
        );

        assertThat(
                bookingRepository
                        .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                                room.getId(),
                                BookingStatus.CONFIRMED,
                                LocalDate.of(2030, 1, 20),
                                LocalDate.of(2030, 1, 15)
                        )
        ).isFalse();
    }

    @Test
    void overlapQuery_shouldIgnoreCancelledBooking() {

        Room room = room();

        booking(
                room,
                LocalDate.of(2030, 1, 10),
                LocalDate.of(2030, 1, 15),
                BookingStatus.CANCELLED
        );

        assertThat(
                bookingRepository
                        .existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                                room.getId(),
                                BookingStatus.CONFIRMED,
                                LocalDate.of(2030, 1, 12),
                                LocalDate.of(2030, 1, 11)
                        )
        ).isFalse();
    }

    @Test
    void findByRoom_shouldOrderByCheckIn() {

        Room room = room();

        booking(
                room,
                LocalDate.of(2030, 1, 20),
                LocalDate.of(2030, 1, 22),
                BookingStatus.CONFIRMED
        );

        booking(
                room,
                LocalDate.of(2030, 1, 10),
                LocalDate.of(2030, 1, 12),
                BookingStatus.CONFIRMED
        );

        assertThat(
                bookingRepository
                        .findByRoomIdOrderByCheckInAsc(
                                room.getId()
                        )
        )
                .extracting(Booking::getCheckIn)
                .containsExactly(
                        LocalDate.of(2030, 1, 10),
                        LocalDate.of(2030, 1, 20)
                );
    }
}