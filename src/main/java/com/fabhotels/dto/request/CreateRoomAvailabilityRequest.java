package com.fabhotels.dto.request;

import com.fabhotels.enums.AvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request payload for creating room availability")
public class CreateRoomAvailabilityRequest {

    @NotNull(message = "Date is required")
    @Schema(
            description = "Date for which availability is being created",
            example = "2026-09-20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate date;

    @NotNull(message = "Status is required")
    @Schema(
            description = "Availability status for the room",
            example = "AVAILABLE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AvailabilityStatus status;

    public CreateRoomAvailabilityRequest() {
    }

    public LocalDate getDate() {
        return date;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }
}