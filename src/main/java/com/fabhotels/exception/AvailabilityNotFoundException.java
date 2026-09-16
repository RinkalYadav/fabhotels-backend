package com.fabhotels.exception;

public class AvailabilityNotFoundException
        extends RuntimeException {

    public AvailabilityNotFoundException(Long availabilityId) {
        super(
                "Availability record not found with id: "
                        + availabilityId
        );
    }
}