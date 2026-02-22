package com.pastebin.authservice.service.impl;

import com.pastebin.authservice.dto.AuthResponse;
import com.pastebin.authservice.dto.LoginRequest;
import com.pastebin.authservice.dto.RegisterRequest;
import com.pastebin.authservice.dto.TokenResponse;
import com.pastebin.authservice.entity.RefreshToken;
import com.pastebin.authservice.entity.User;
import com.pastebin.authservice.repository.RefreshTokenRepository;
import com.pastebin.authservice.repository.UserRepository;
import com.pastebin.authservice.service.AuthService;
import com.pastebin.authservice.util.JwtTokenProvider;
import com.pastebin.authservice.util.TokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenHasher tokenHasher;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User with email already exists: " + request.email());
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .enabled(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new AuthResponse(
                new TokenResponse(accessToken, refreshToken),
                user.getEmail(),
                user.getId()
        );
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid refresh token");
        }

        UUID userId = jwtTokenProvider.extractUserId(token);

        String tokenHash = tokenHasher.hash(token);

        RefreshToken tokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (tokenEntity.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (tokenEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        saveRefreshToken(user, newRefreshToken);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid refresh token");
        }
        UUID userId = jwtTokenProvider.extractUserId(token);
        refreshTokenRepository.deleteByUserId(userId);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .tokenHash(tokenHasher.hash(token))
                .expiresAt(Instant.now().plusMillis(604800000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }
}