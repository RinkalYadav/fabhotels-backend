package com.fabhotels.dto.response;

import com.fabhotels.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Response containing booking cancellation details")
public class CancellationResponse {

    @Schema(description = "ID of the cancelled booking", example = "101")
    private Long bookingId;

    @Schema(description = "Current booking status", example = "CANCELLED")
    private BookingStatus status;

    @Schema(description = "Date and time when the booking was cancelled", example = "2026-09-18T11:30:00")
    private LocalDateTime cancelledAt;

    @Schema(description = "Refund amount issued for the cancellation", example = "2500.00")
    private BigDecimal refundAmount;

    @Schema(description = "Reason provided for cancellation", example = "Change of travel plans")
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