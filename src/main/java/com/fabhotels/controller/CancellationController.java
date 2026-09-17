package com.fabhotels.controller;

import com.fabhotels.dto.request.CancelBookingRequest;
import com.fabhotels.dto.response.CancellationResponse;
import com.fabhotels.service.CancellationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(
            CancellationService cancellationService
    ) {
        this.cancellationService = cancellationService;
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancellationResponse> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) CancelBookingRequest request
    ) {

        CancellationResponse response =
                cancellationService.cancelBooking(
                        bookingId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}