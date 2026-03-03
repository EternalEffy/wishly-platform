package com.wishly.authservice.service;

import com.wishly.authservice.dto.AuthResponse;
import com.wishly.authservice.dto.LoginRequest;
import com.wishly.authservice.dto.RegisterRequest;
import com.wishly.authservice.dto.TokenResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse refreshToken(String token);

    void logout(String token);
}
