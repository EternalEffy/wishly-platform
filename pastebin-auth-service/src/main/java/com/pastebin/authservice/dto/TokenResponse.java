package com.pastebin.authservice.dto;

import com.pastebin.authservice.entity.RefreshToken;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
