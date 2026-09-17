package com.fabhotels.dto.request;

public class CancelBookingRequest {

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