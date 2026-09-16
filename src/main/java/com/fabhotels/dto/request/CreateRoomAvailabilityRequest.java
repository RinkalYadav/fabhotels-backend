package com.fabhotels.dto.request;

import com.fabhotels.enums.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateRoomAvailabilityRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
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