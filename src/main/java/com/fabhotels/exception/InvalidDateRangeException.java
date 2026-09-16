package com.fabhotels.exception;

public class InvalidDateRangeException
        extends RuntimeException {

    public InvalidDateRangeException() {
        super("Check-out date must be after check-in date");
    }
}