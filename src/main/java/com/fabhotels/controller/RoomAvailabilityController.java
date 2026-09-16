package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateRoomAvailabilityRequest;
import com.fabhotels.dto.response.RoomAvailabilityCheckResponse;
import com.fabhotels.dto.response.RoomAvailabilityResponse;
import com.fabhotels.service.RoomAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomAvailabilityController {

    private final RoomAvailabilityService availabilityService;

    public RoomAvailabilityController(
            RoomAvailabilityService availabilityService
    ) {
        this.availabilityService =
                availabilityService;
    }

    @PostMapping("/{roomId}/availability")
    public ResponseEntity<RoomAvailabilityResponse>
    createAvailability(
            @PathVariable Long roomId,
            @Valid @RequestBody
            CreateRoomAvailabilityRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        availabilityService.createAvailability(
                                roomId,
                                request
                        )
                );
    }

    @GetMapping("/{roomId}/availability")
    public ResponseEntity<List<RoomAvailabilityResponse>>
    getAvailability(
            @PathVariable Long roomId
    ) {

        return ResponseEntity.ok(
                availabilityService
                        .getRoomAvailability(roomId)
        );
    }

    @GetMapping("/{roomId}/availability/range")
    public ResponseEntity<List<RoomAvailabilityResponse>>
    getAvailabilityByDateRange(
            @PathVariable Long roomId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return ResponseEntity.ok(
                availabilityService
                        .getAvailabilityByDateRange(
                                roomId,
                                from,
                                to
                        )
        );
    }

    @GetMapping("/{roomId}/availability/check")
    public ResponseEntity<RoomAvailabilityCheckResponse>
    checkAvailability(
            @PathVariable Long roomId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut
    ) {

        return ResponseEntity.ok(
                availabilityService.checkAvailability(
                        roomId,
                        checkIn,
                        checkOut
                )
        );
    }
}