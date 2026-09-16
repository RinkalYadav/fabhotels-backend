package com.fabhotels.repository;

import com.fabhotels.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    boolean existsByBookingId(Long bookingId);

    Optional<Payment> findByBookingId(Long bookingId);
}