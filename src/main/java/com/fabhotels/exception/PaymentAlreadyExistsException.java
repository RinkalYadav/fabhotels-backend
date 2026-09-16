package com.fabhotels.exception;

public class PaymentAlreadyExistsException extends RuntimeException {

    public PaymentAlreadyExistsException(Long bookingId) {
        super(
                "Payment already exists for booking id: "
                        + bookingId
        );
    }
}