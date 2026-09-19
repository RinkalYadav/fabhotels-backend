package com.fabhotels.service;

import com.fabhotels.dto.request.LoginRequest;
import com.fabhotels.dto.request.RegisterRequest;
import com.fabhotels.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}