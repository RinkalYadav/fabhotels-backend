package com.fabhotels.exception;

public class GuestCapacityExceededException extends RuntimeException {

    public GuestCapacityExceededException(String message) {
        super(message);
    }
}