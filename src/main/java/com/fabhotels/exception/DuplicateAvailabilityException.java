package com.fabhotels.exception;

public class DuplicateAvailabilityException
        extends RuntimeException {

    public DuplicateAvailabilityException(
            Long roomId,
            String date
    ) {
        super(
                "Availability already exists for room "
                        + roomId
                        + " on date "
                        + date
        );
    }
}