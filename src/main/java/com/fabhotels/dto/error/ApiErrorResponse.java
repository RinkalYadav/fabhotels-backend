package com.fabhotels.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Standard API error response")
public class ApiErrorResponse {

    @Schema(
            description = "Date and time when the error occurred",
            example = "2026-09-18T11:30:00"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "HTTP status code",
            example = "404"
    )
    private int status;

    @Schema(
            description = "HTTP status description",
            example = "Not Found"
    )
    private String error;

    @Schema(
            description = "Error message",
            example = "Room with id 10 not found"
    )
    private String message;

    @Schema(
            description = "Request path where the error occurred",
            example = "/api/rooms/10"
    )
    private String path;

    @Schema(
            description = "Validation errors mapped by field name"
    )
    private Map<String, String> fieldErrors;

    @Schema(
            description = "Unique identifier used to trace the request in application logs",
            example = "7f8a9c21-4d3b-4c7a-9f21-123456789abc"
    )
    private String correlationId;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}