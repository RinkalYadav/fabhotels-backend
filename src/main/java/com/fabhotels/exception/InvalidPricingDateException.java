package com.fabhotels.exception;

public class InvalidPricingDateException extends RuntimeException {

    public InvalidPricingDateException(String message) {
        super(message);
    }
}