package com.fabhotels.controller;

import com.fabhotels.dto.response.PaymentResponse;
import com.fabhotels.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Booking Payments",
        description = "Booking payment lookup APIs"
)
@RestController
@RequestMapping("/api/bookings")
public class BookingPaymentController {

    private final PaymentService paymentService;

    public BookingPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Get payment by booking ID",
            description = "Returns payment details associated with the specified booking."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found for the specified booking"
            )
    })
    @GetMapping("/{bookingId}/payment")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(

            @Parameter(
                    description = "Unique booking ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(bookingId)
        );
    }
}