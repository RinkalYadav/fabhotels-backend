package com.fabhotels.exception;

import com.fabhotels.dto.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.MDC;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================================================
    // 404 - RESOURCE NOT FOUND
    // =========================================================

    @ExceptionHandler(HotelNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleHotelNotFound(
            HotelNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoomNotFound(
            RoomNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAvailabilityNotFound(
            AvailabilityNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotFound(
            BookingNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentNotFound(
            PaymentNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // =========================================================
    // 409 - CONFLICT
    // =========================================================

    @ExceptionHandler(DuplicateRoomException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateRoom(
            DuplicateRoomException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicatePricingException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicatePricing(
            DuplicatePricingException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(PaymentAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentAlreadyExists(
            PaymentAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BookingAlreadyCancelledException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingAlreadyCancelled(
            BookingAlreadyCancelledException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // =========================================================
    // 400 - BUSINESS VALIDATION
    // =========================================================

    @ExceptionHandler(InvalidBookingDateException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBookingDate(
            InvalidBookingDateException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidCancellationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCancellation(
            InvalidCancellationException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPaymentAmount(
            InvalidPaymentAmountException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BookingNotPayableException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotPayable(
            BookingNotPayableException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // =========================================================
    // 400 - BEAN VALIDATION
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> fieldErrors =
                new LinkedHashMap<>();

        for (FieldError fieldError :
                ex.getBindingResult().getFieldErrors()) {

            fieldErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ApiErrorResponse response =
                buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        request.getRequestURI()
                );

        response.setFieldErrors(fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // =========================================================
    // 400 - MALFORMED JSON / INVALID ENUM
    // =========================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed or invalid request body",
                request.getRequestURI()
        );
    }

    // =========================================================
    // 405 - METHOD NOT ALLOWED
    // =========================================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method " + ex.getMethod()
                        + " is not supported for this endpoint",
                request.getRequestURI()
        );
    }

    // =========================================================
    // 404 - UNKNOWN ENDPOINT
    // =========================================================

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Endpoint not found",
                request.getRequestURI()
        );
    }

    // =========================================================
    // 500 - UNEXPECTED ERROR
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(
                "Unexpected error while processing request: {}",
                request.getRequestURI(),
                ex
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path
    ) {

        String correlationId = MDC.get("correlationId");

        log.warn(
                "Request failed: status={} correlationId={} path={} message={}",
                status.value(),
                correlationId,
                path,
                message
        );

        ApiErrorResponse response =
                buildErrorResponse(
                        status,
                        message,
                        path
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private ApiErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            String path
    ) {
        String correlationId = MDC.get("correlationId");

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        response.setCorrelationId(correlationId);

        return response;
    }
}