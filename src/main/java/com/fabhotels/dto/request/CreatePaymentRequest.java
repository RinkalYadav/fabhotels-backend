package com.fabhotels.dto.request;

import com.fabhotels.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Request payload for creating a payment")
public class CreatePaymentRequest {

    @Schema(
            description = "Unique booking ID for which payment is being made",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long bookingId;

    @Schema(
            description = "Payment amount",
            example = "5998.00",
            minimum = "0.01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal amount;

    @Schema(
            description = "Payment method",
            example = "UPI",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PaymentMethod paymentMethod;

    public CreatePaymentRequest() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}