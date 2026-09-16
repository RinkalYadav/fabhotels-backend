package com.fabhotels.dto.response;

import java.time.LocalDate;

public class RoomAvailabilityCheckResponse {

    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
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