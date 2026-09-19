package com.fabhotels.controller;

import com.fabhotels.dto.request.LoginRequest;
import com.fabhotels.dto.request.RegisterRequest;
import com.fabhotels.dto.response.AuthResponse;
import com.fabhotels.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Authentication",
        description = "Registration and JWT authentication APIs"
)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService =
                authService;
    }

    @Operation(
            summary = "Register a customer"
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Operation(
            summary = "Login and generate JWT"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(
                        request
                )
        );
    }
}