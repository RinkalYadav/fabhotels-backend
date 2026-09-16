package com.fabhotels.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HotelNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleHotelNotFound(
            HotelNotFoundException exception
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRoomNotFound(
            RoomNotFoundException exception
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DuplicateRoomException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateRoom(
            DuplicateRoomException exception
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<Map<String, String>>
    handleAvailabilityNotFound(
            AvailabilityNotFoundException exception
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DuplicateAvailabilityException.class)
    public ResponseEntity<Map<String, String>>
    handleDuplicateAvailability(
            DuplicateAvailabilityException exception
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Map<String, String>>
    handleInvalidDateRange(
            InvalidDateRangeException exception
    ) {

        Map<String, String> response = new HashMap<>();

        response.put("error", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidSearchParameterException.class)
    public ResponseEntity<Map<String, String>>
    handleInvalidSearchParameter(
            InvalidSearchParameterException exception
    ) {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "error",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}