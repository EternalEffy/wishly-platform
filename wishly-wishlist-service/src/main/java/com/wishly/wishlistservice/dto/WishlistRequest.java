package com.wishly.wishlistservice.dto;

import com.wishly.wishlistservice.model.enums.Privacy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record WishlistRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        String title,
        @NotNull(message = "Privacy is required")
        Privacy privacy,
        LocalDateTime eventDate,
        Boolean keepArchived
) {
}
