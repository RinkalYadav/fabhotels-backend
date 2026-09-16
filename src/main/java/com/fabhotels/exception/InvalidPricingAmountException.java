package com.fabhotels.exception;

public class InvalidPricingAmountException extends RuntimeException {

    public InvalidPricingAmountException(String message) {
        super(message);
    }
}