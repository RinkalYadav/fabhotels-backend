package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateBookingRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Bookings",
        description = "Hotel room booking APIs"
)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Create a booking",
            description = "Creates a new hotel room booking after validating room availability, guest capacity, booking dates, and pricing."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Booking created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid booking request, dates, guest count, or room availability"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room or required resource not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Room is already booked for the requested dates"
            )
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Booking details",
                    required = true
            )
            @RequestBody CreateBookingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    @Operation(
            summary = "Get booking by ID",
            description = "Returns the booking details for the specified booking ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            )
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @Parameter(
                    description = "Unique booking ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBookingById(bookingId)
        );
    }
}