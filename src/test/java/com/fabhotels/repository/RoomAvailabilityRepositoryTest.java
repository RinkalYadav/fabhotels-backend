package com.fabhotels.repository;

import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Room;
import com.fabhotels.entity.RoomAvailability;
import com.fabhotels.enums.AvailabilityStatus;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomAvailabilityRepositoryTest {

    @Autowired
    private RoomAvailabilityRepository repository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Room room() {

        Hotel hotel =
                hotelRepository.save(
                        new Hotel(
                                null,
                                "Hotel",
                                "d",
                                "a",
                                "Bengaluru",
                                "Karnataka",
                                "India",
                                "560001",
                                true,
                                null
                        )
                );

        return roomRepository.save(
                new Room(
                        null,
                        hotel,
                        "101",
                        RoomType.DOUBLE,
                        new BigDecimal("2000"),
                        2,
                        RoomStatus.AVAILABLE,
                        null
                )
        );
    }

    private void add(
            Room room,
            LocalDate date,
            AvailabilityStatus status
    ) {

        RoomAvailability availability =
                new RoomAvailability();

        availability.setRoom(room);
        availability.setDate(date);
        availability.setStatus(status);

        repository.save(availability);
    }

    @Test
    void findByRoomAndDate_shouldReturnRecord() {

        Room room = room();

        add(
                room,
                LocalDate.of(2030, 1, 1),
                AvailabilityStatus.AVAILABLE
        );

        assertThat(
                repository.findByRoomIdAndDate(
                        room.getId(),
                        LocalDate.of(2030, 1, 1)
                )
        ).isPresent();
    }

    @Test
    void dateBetween_shouldReturnOnlyInclusiveRange() {

        Room room = room();

        add(
                room,
                LocalDate.of(2030, 1, 1),
                AvailabilityStatus.AVAILABLE
        );

        add(
                room,
                LocalDate.of(2030, 1, 2),
                AvailabilityStatus.BOOKED
        );

        add(
                room,
                LocalDate.of(2030, 1, 4),
                AvailabilityStatus.AVAILABLE
        );

        assertThat(
                repository.findByRoomIdAndDateBetweenOrderByDateAsc(
                        room.getId(),
                        LocalDate.of(2030, 1, 2),
                        LocalDate.of(2030, 1, 3)
                )
        ).hasSize(1);
    }

    @Test
    void stayRange_shouldBeCheckOutExclusive() {

        Room room = room();

        add(
                room,
                LocalDate.of(2030, 1, 1),
                AvailabilityStatus.AVAILABLE
        );

        add(
                room,
                LocalDate.of(2030, 1, 2),
                AvailabilityStatus.AVAILABLE
        );

        add(
                room,
                LocalDate.of(2030, 1, 3),
                AvailabilityStatus.BLOCKED
        );

        assertThat(
                repository
                        .findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                                room.getId(),
                                LocalDate.of(2030, 1, 1),
                                LocalDate.of(2030, 1, 3)
                        )
        ).hasSize(2);
    }
}