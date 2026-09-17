package com.fabhotels.exception;

import com.fabhotels.dto.error.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleBookingNotFound() {

        BookingNotFoundException exception =
                new BookingNotFoundException(101L);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/api/bookings/101"
        );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleBookingNotFound(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                404,
                response.getBody().getStatus()
        );

        assertEquals(
                "Not Found",
                response.getBody().getError()
        );

        assertEquals(
                "Booking not found with id: 101",
                response.getBody().getMessage()
        );

        assertEquals(
                "/api/bookings/101",
                response.getBody().getPath()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );
    }

    @Test
    void shouldHandlePaymentAlreadyExists() {

        PaymentAlreadyExistsException exception =
                new PaymentAlreadyExistsException(101L);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/api/payments"
        );

        ResponseEntity<ApiErrorResponse> response =
                handler.handlePaymentAlreadyExists(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                409,
                response.getBody().getStatus()
        );

        assertEquals(
                "Conflict",
                response.getBody().getError()
        );

        assertEquals(
                "Payment already exists for booking id: 101",
                response.getBody().getMessage()
        );
    }

    @Test
    void shouldHandleInvalidPaymentAmount() {

        InvalidPaymentAmountException exception =
                new InvalidPaymentAmountException(
                        "Payment amount must be greater than 0"
                );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/api/payments"
        );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleInvalidPaymentAmount(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                400,
                response.getBody().getStatus()
        );

        assertEquals(
                "Bad Request",
                response.getBody().getError()
        );

        assertEquals(
                "Payment amount must be greater than 0",
                response.getBody().getMessage()
        );
    }
}