package com.fabhotels.repository;

import com.fabhotels.entity.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Long> {

    List<Pricing> findByRoomIdOrderByStartDateAsc(Long roomId);

    Optional<Pricing>
    findFirstByRoomIdAndStartDateLessThanEqualAndEndDateGreaterThanAndActiveTrue(
            Long roomId,
            LocalDate date1,
            LocalDate date2
    );

    boolean existsByRoomIdAndActiveTrueAndStartDateLessThanAndEndDateGreaterThan(
            Long roomId,
            LocalDate endDate,
            LocalDate startDate
    );
}