package com.fabhotels.dto.response;

import com.fabhotels.enums.PaymentMethod;
import com.fabhotels.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Response containing payment details")
public class PaymentResponse {

    @Schema(description = "Unique payment ID", example = "501")
    private Long id;

    @Schema(description = "ID of the associated booking", example = "101")
    private Long bookingId;

    @Schema(description = "Payment amount", example = "5000.00")
    private BigDecimal amount;

    @Schema(description = "Payment method", example = "UPI")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment status", example = "SUCCESS")
    private PaymentStatus status;

    @Schema(description = "Unique transaction ID", example = "TXN-20260918103000123")
    private String transactionId;

    @Schema(description = "Date and time when payment was completed", example = "2026-09-18T10:30:00")
    private LocalDateTime paidAt;

    @Schema(description = "Date and time when payment record was created", example = "2026-09-18T10:30:00")
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            Long bookingId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            String transactionId,
            LocalDateTime paidAt,
            LocalDateTime createdAt) {

        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}