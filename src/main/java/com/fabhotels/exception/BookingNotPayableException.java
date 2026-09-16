package com.fabhotels.exception;

public class BookingNotPayableException
        extends RuntimeException {

    public BookingNotPayableException(String message) {
        super(message);
    }
}