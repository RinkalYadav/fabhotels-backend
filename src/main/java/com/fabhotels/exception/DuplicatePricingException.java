package com.fabhotels.exception;

public class DuplicatePricingException extends RuntimeException {

    public DuplicatePricingException(String message) {
        super(message);
    }
}