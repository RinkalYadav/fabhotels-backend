package com.fabhotels.dto.response;

import com.fabhotels.enums.AvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response containing room availability details")
public class RoomAvailabilityResponse {

    @Schema(
            description = "Unique availability record ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Unique room ID",
            example = "1"
    )
    private Long roomId;

    @Schema(
            description = "Date for which availability is recorded",
            example = "2026-09-20"
    )
    private LocalDate date;

    @Schema(
            description = "Availability status of the room",
            example = "AVAILABLE"
    )
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