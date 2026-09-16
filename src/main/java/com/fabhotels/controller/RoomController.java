package com.fabhotels.controller;

import com.fabhotels.dto.request.CreateRoomRequest;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.dto.response.RoomResponse;
import com.fabhotels.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.fabhotels.dto.response.BookingResponse;
import com.fabhotels.service.BookingService;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;

    public RoomController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<RoomResponse> createRoom(
            @PathVariable Long hotelId,
            @Valid @RequestBody CreateRoomRequest request
    ) {

        RoomResponse response =
                roomService.createRoom(hotelId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
            @PathVariable Long roomId
    ) {

        return ResponseEntity.ok(
                roomService.getRoomById(roomId)
        );
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotelId(
            @PathVariable Long hotelId
    ) {

        return ResponseEntity.ok(
                roomService.getRoomsByHotelId(hotelId)
        );
    }

    @GetMapping("/{roomId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByRoom(
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                bookingService.getBookingsByRoom(roomId)
        );
    }
}