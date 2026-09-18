package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateRoomAvailabilityRequest;
import com.fabhotels.dto.response.RoomAvailabilityCheckResponse;
import com.fabhotels.dto.response.RoomAvailabilityResponse;
import com.fabhotels.service.RoomAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Room Availability",
        description = "Room availability management and checking APIs"
)
@RestController
@RequestMapping("/api/rooms")
public class RoomAvailabilityController {

    private final RoomAvailabilityService availabilityService;

    public RoomAvailabilityController(
            RoomAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Operation(
            summary = "Create room availability",
            description = "Creates an availability record for a room on a specific date."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Availability created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid availability data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Availability already exists for the room and date"
            )
    })
    @PostMapping("/{roomId}/availability")
    public ResponseEntity<RoomAvailabilityResponse> createAvailability(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Room availability details",
                    required = true
            )
            @Valid @RequestBody CreateRoomAvailabilityRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        availabilityService.createAvailability(
                                roomId,
                                request
                        )
                );
    }

    @Operation(
            summary = "Get room availability",
            description = "Returns all availability records for the specified room."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability records retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            )
    })
    @GetMapping("/{roomId}/availability")
    public ResponseEntity<List<RoomAvailabilityResponse>> getAvailability(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                availabilityService.getRoomAvailability(roomId)
        );
    }

    @Operation(
            summary = "Get room availability by date range",
            description = "Returns availability records between the specified start and end dates."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability records retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            )
    })
    @GetMapping("/{roomId}/availability/range")
    public ResponseEntity<List<RoomAvailabilityResponse>>
    getAvailabilityByDateRange(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId,

            @Parameter(
                    description = "Start date",
                    example = "2026-09-20",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(
                    description = "End date",
                    example = "2026-09-25",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return ResponseEntity.ok(
                availabilityService.getAvailabilityByDateRange(
                        roomId,
                        from,
                        to
                )
        );
    }

    @Operation(
            summary = "Check room availability",
            description = "Checks whether the room is available for all nights between check-in and check-out."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability check completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid check-in or check-out dates"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            )
    })
    @GetMapping("/{roomId}/availability/check")
    public ResponseEntity<RoomAvailabilityCheckResponse>
    checkAvailability(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId,

            @Parameter(
                    description = "Check-in date",
                    example = "2026-09-20",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,

            @Parameter(
                    description = "Check-out date",
                    example = "2026-09-25",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut) {

        return ResponseEntity.ok(
                availabilityService.checkAvailability(
                        roomId,
                        checkIn,
                        checkOut
                )
        );
    }
}