package com.fabhotels.dto.request;

import com.fabhotels.enums.PaymentMethod;

import java.math.BigDecimal;

public class CreatePaymentRequest {

    private Long bookingId;

    private BigDecimal amount;

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