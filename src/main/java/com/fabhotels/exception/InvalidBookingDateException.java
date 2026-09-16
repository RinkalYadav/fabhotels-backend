package com.fabhotels.exception;

public class InvalidBookingDateException extends RuntimeException {

    public InvalidBookingDateException(String message) {
        super(message);
    }
}