package com.fabhotels.dto.response;

import com.fabhotels.enums.AvailabilityStatus;

import java.time.LocalDate;

public class RoomAvailabilityResponse {

    private Long id;
    private Long roomId;
    private LocalDate date;
    private AvailabilityStatus status;

    public RoomAvailabilityResponse() {
    }

    public RoomAvailabilityResponse(
            Long id,
            Long roomId,
            LocalDate date,
            AvailabilityStatus status
    ) {
        this.id = id;
        this.roomId = roomId;
        this.date = date;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDate getDate() {
        return date;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }
}