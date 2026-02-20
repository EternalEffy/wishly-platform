package com.pastebin.pasteservice.dto;

import java.time.Instant;

public record PasteResponse(
        String hash,
        String content,
        Instant createdAt,
        Instant expiresAt
) {
}