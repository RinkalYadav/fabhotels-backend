package com.fabhotels.repository;

import com.fabhotels.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomAvailabilityRepository
        extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findByRoomIdOrderByDateAsc(
            Long roomId
    );

    Optional<RoomAvailability> findByRoomIdAndDate(
            Long roomId,
            LocalDate date
    );

    boolean existsByRoomIdAndDate(
            Long roomId,
            LocalDate date
    );

    List<RoomAvailability> findByRoomIdAndDateBetweenOrderByDateAsc(
            Long roomId,
            LocalDate from,
            LocalDate to
    );

    List<RoomAvailability>
    findByRoomIdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );
}