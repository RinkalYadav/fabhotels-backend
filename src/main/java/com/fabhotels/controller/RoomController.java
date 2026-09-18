package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateRoomRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.dto.response.RoomResponse;
import com.fabhotels.service.BookingService;
import com.fabhotels.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Rooms",
        description = "Room management APIs"
)
@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;

    public RoomController(
            RoomService roomService,
            BookingService bookingService) {

        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Create a room",
            description = "Creates a new room for the specified hotel."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Room created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid room data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hotel not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Room already exists for the hotel"
            )
    })
    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<RoomResponse> createRoom(

            @Parameter(
                    description = "Unique hotel ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long hotelId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Room details",
                    required = true
            )
            @Valid @RequestBody CreateRoomRequest request) {

        RoomResponse response =
                roomService.createRoom(hotelId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get room by ID",
            description = "Returns the details of a room using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Room retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            )
    })
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                roomService.getRoomById(roomId)
        );
    }

    @Operation(
            summary = "Get rooms by hotel",
            description = "Returns all rooms belonging to the specified hotel."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rooms retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Hotel not found"
            )
    })
    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotelId(

            @Parameter(
                    description = "Unique hotel ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long hotelId) {

        return ResponseEntity.ok(
                roomService.getRoomsByHotelId(hotelId)
        );
    }

    @Operation(
            summary = "Get bookings by room",
            description = "Returns all bookings associated with the specified room."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bookings retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Room not found"
            )
    })
    @GetMapping("/{roomId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByRoom(

            @Parameter(
                    description = "Unique room ID",
                    example = "1",
                    required = true
            )
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByRoom(roomId)
        );
    }
}