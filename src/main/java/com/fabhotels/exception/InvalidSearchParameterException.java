package com.fabhotels.exception;

public class InvalidSearchParameterException extends RuntimeException {

    public InvalidSearchParameterException(String message) {
        super(message);
    }
}