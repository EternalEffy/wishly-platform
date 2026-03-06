package com.wishly.wishlistservice.dto;

import com.wishly.wishlistservice.model.enums.PriorityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record GiftItemResponse(
        UUID id,
        String name,
        PriorityLevel priority,
        String productUrl,
        String description,
        boolean reserved,
        String reservedByName,
        String reservedByEmail,
        LocalDateTime reservedAt,
        LocalDateTime createdAt
) {}
