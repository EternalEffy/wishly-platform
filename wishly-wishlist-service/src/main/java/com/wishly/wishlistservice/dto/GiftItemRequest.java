package com.wishly.wishlistservice.dto;


import com.wishly.wishlistservice.model.enums.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record GiftItemRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
        String name,

        @NotNull(message = "Priority is required")
        PriorityLevel priority,

        @NotBlank(message = "Product URL is required")
        @URL(message = "Invalid URL format")
        String productUrl,

        @Size(max = 1000, message = "Description too long")
        String description
) {
}
