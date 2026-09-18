package com.fabhotels.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for cancelling a booking")
public class CancelBookingRequest {

    @Schema(
            description = "Optional reason for cancelling the booking",
            example = "Change of travel plans"
    )
    private String reason;

    public CancelBookingRequest() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}