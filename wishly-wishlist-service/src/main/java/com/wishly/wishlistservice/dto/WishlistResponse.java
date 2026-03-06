package com.wishly.wishlistservice.dto;

import com.wishly.wishlistservice.model.enums.Privacy;

import java.time.LocalDateTime;
import java.util.UUID;

public record WishlistResponse(
        UUID id,
        String title,
        UUID ownerId,
        Privacy privacy,
        LocalDateTime eventDate,
        LocalDateTime createdAt,
        long itemCount,
        boolean archived
) {
}
