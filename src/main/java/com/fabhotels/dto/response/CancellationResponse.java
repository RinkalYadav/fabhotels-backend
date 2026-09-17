package com.fabhotels.dto.response;

import com.fabhotels.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CancellationResponse {

    private Long bookingId;
    private BookingStatus status;
    private LocalDateTime cancelledAt;
    private BigDecimal refundAmount;
    private String reason;

    public CancellationResponse(
            Long bookingId,
            BookingStatus status,
            LocalDateTime cancelledAt,
            BigDecimal refundAmount,
            String reason
    ) {
        this.bookingId = bookingId;
        this.status = status;
        this.cancelledAt = cancelledAt;
        this.refundAmount = refundAmount;
        this.reason = reason;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public String getReason() {
        return reason;
    }
}