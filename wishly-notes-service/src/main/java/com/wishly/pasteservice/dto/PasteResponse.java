package com.wishly.pasteservice.dto;

import java.time.Instant;

public record PasteResponse(
        String hash,
        String content,
        Instant createdAt,
        Instant expiresAt
) {
}