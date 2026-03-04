package com.wishly.wishlistservice.dto;

import com.wishly.wishlistservice.model.enums.PriorityLevel;

import java.math.BigDecimal;
import java.util.UUID;

public record GiftItemResponse(
        UUID id,
        String name,
        PriorityLevel priority,
        boolean reserved,
        String productUrl,
        String imageUrl,
        BigDecimal price,
        String currency,
        String siteName,
        String description
) {
}
