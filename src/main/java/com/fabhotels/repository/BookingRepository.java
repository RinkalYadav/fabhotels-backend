package com.fabhotels.repository;

import com.fabhotels.entity.Booking;
import com.fabhotels.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
            Long roomId,
            BookingStatus status,
            LocalDate checkOut,
            LocalDate checkIn
    );

    List<Booking> findByRoomIdOrderByCheckInAsc(Long roomId);
}