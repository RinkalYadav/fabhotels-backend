package com.fabhotels.service.impl;

import com.fabhotels.dto.request.LoginRequest;
import com.fabhotels.dto.request.RegisterRequest;
import com.fabhotels.dto.response.AuthResponse;
import com.fabhotels.entity.User;
import com.fabhotels.enums.UserRole;
import com.fabhotels.repository.UserRepository;
import com.fabhotels.security.JwtService;
import com.fabhotels.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.authenticationManager =
                authenticationManager;

        this.jwtService =
                jwtService;
    }

    @Override
    @Transactional
    public void register(
            RegisterRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (userRepository
                .existsByEmailIgnoreCase(email)) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        User user =
                new User();

        user.setName(
                request.getName().trim()
        );

        user.setEmail(email);

        /*
         * NEVER store raw password.
         */
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        /*
         * Public registration can only
         * create CUSTOMER accounts.
         */
        user.setRole(
                UserRole.CUSTOMER
        );

        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(
            LoginRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        User user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid email or password"
                                )
                        );

        String token =
                jwtService.generateToken(
                        user
                );

        return new AuthResponse(
                token
        );
    }
}