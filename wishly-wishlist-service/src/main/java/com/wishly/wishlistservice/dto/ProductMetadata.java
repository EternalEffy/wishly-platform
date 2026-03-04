package com.wishly.wishlistservice.dto;

import java.math.BigDecimal;

public record ProductMetadata(
        String productUrl,
        String title,
        String description,
        String imageUrl,
        BigDecimal price,
        String currency,
        String siteName,
        String urlHash
) {}
