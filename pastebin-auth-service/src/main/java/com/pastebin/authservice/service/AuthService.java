package com.pastebin.authservice.service;

import com.pastebin.authservice.dto.AuthResponse;
import com.pastebin.authservice.dto.LoginRequest;
import com.pastebin.authservice.dto.RegisterRequest;
import com.pastebin.authservice.dto.TokenResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse refreshToken(String token);

    void logout(String token);
}
