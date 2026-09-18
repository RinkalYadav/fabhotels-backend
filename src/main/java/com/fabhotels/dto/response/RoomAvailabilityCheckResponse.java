package com.fabhotels.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response containing room availability check result")
public class RoomAvailabilityCheckResponse {

    @Schema(
            description = "Unique room ID",
            example = "1"
    )
    private Long roomId;

    @Schema(
            description = "Requested check-in date",
            example = "2026-09-20"
    )
    private LocalDate checkIn;

    @Schema(
            description = "Requested check-out date",
            example = "2026-09-25"
    )
    private LocalDate checkOut;

    @Schema(
            description = "Indicates whether the room is available for the requested stay",
            example = "true"
    )
    private boolean available;

    public RoomAvailabilityCheckResponse() {
    }

    public RoomAvailabilityCheckResponse(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut,
            boolean available
    ) {
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.available = available;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public boolean isAvailable() {
        return available;
    }
}