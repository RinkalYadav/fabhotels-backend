package com.fabhotels.controller;

import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingPaymentController {

    private final PaymentService paymentService;

    public BookingPaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @GetMapping("/{bookingId}/payment")
    public ResponseEntity<PaymentResponse>
    getPaymentByBookingId(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(
                        bookingId
                )
        );
    }
}