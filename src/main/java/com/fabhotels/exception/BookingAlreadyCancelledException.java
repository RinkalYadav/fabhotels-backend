package com.fabhotels.exception;

public class BookingAlreadyCancelledException extends RuntimeException {

    public BookingAlreadyCancelledException(Long bookingId) {
        super("Booking is already cancelled with id: " + bookingId);
    }
}