package com.fabhotels.repository;

import com.fabhotels.entity.Hotel;
import com.fabhotels.entity.Pricing;
import com.fabhotels.entity.Room;
import com.fabhotels.enums.RoomStatus;
import com.fabhotels.enums.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PricingRepositoryTest {

    @Autowired
    private PricingRepository pricingRepository;

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

    private Pricing pricing(
            Room room,
            LocalDate start,
            LocalDate end,
            boolean active,
            BigDecimal amount
    ) {

        Pricing pricing = new Pricing();

        pricing.setRoom(room);
        pricing.setStartDate(start);
        pricing.setEndDate(end);
        pricing.setPricePerNight(amount);
        pricing.setActive(active);

        return pricingRepository.save(pricing);
    }

    @Test
    void overlapQuery_shouldDetectActiveOverlap() {

        Room room = room();

        pricing(
                room,
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 1, 10),
                true,
                new BigDecimal("2000")
        );

        assertThat(
                pricingRepository
                        .existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
                                room.getId(),
                                LocalDate.of(2030, 1, 8),
                                LocalDate.of(2030, 1, 5)
                        )
        ).isTrue();
    }

    @Test
    void overlapQuery_shouldAllowAdjacentRange() {

        Room room = room();

        pricing(
                room,
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 1, 10),
                true,
                new BigDecimal("2000")
        );

        assertThat(
                pricingRepository
                        .existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
                                room.getId(),
                                LocalDate.of(2030, 1, 20),
                                LocalDate.of(2030, 1, 10)
                        )
        ).isFalse();
    }

    @Test
    void lookup_shouldUseStartInclusiveEndExclusive() {

        Room room = room();

        pricing(
                room,
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 1, 10),
                true,
                new BigDecimal("2000")
        );

        assertThat(
                pricingRepository
                        .findFirstByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanAndActiveTrue(
                                room.getId(),
                                LocalDate.of(2030, 1, 9),
                                LocalDate.of(2030, 1, 9)
                        )
        ).isPresent();

        assertThat(
                pricingRepository
                        .findFirstByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanAndActiveTrue(
                                room.getId(),
                                LocalDate.of(2030, 1, 10),
                                LocalDate.of(2030, 1, 10)
                        )
        ).isEmpty();
    }

    @Test
    void lookup_shouldIgnoreInactivePricing() {

        Room room = room();

        pricing(
                room,
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 1, 10),
                false,
                new BigDecimal("2000")
        );

        assertThat(
                pricingRepository
                        .findFirstByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanAndActiveTrue(
                                room.getId(),
                                LocalDate.of(2030, 1, 5),
                                LocalDate.of(2030, 1, 5)
                        )
        ).isEmpty();
    }
}