package com.fabhotels.exception;

public class DuplicateRoomException extends RuntimeException {

    public DuplicateRoomException(
            Long hotelId,
            String roomNumber
    ) {
        super(
                "Room " + roomNumber +
                        " already exists in hotel " + hotelId
        );
    }
}