package com.fabhotels.controller;

import com.fabhotels.dto.request.CancelBookingRequest;
import com.fabhotels.dto.response.CancellationResponse;
import com.fabhotels.service.CancellationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Booking Cancellation",
        description = "Booking cancellation and refund APIs"
)
@RestController
@RequestMapping("/api/bookings")
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(
            CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @Operation(
            summary = "Cancel a booking",
            description = "Cancels a confirmed booking, releases the room availability, and calculates the applicable refund amount based on the cancellation time."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid cancellation request or booking cannot be cancelled"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking or required resource not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Booking has already been cancelled"
            )
    })
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancellationResponse> cancelBooking(

            @Parameter(
                    description = "Unique booking ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long bookingId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Optional cancellation reason. The request body can be omitted.",
                    required = false
            )
            @RequestBody(required = false) CancelBookingRequest request) {

        CancellationResponse response =
                cancellationService.cancelBooking(
                        bookingId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}