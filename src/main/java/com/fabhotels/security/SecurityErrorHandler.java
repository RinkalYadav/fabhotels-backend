package com.fabhotels.security;

import com.fabhotels.dto.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class SecurityErrorHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public SecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        writeError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Authentication is required",
                request
        );
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        writeError(
                response,
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource",
                request
        );
    }

    private void writeError(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) throws IOException {

        String correlationId = MDC.get("correlationId");

        ApiErrorResponse error =
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()
                );

        error.setCorrelationId(correlationId);

        response.setStatus(status.value());
        response.setContentType("application/json");

        if (correlationId != null) {
            response.setHeader(
                    "X-Correlation-ID",
                    correlationId
            );
        }

        response.getWriter().write(
                objectMapper.writeValueAsString(error)
        );
    }
}