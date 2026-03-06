package com.wishly.wishlistservice.dto;


import com.wishly.wishlistservice.model.enums.PriorityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record GiftItemRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255)
        String name,

        PriorityLevel priority,

        @Size(max = 2048, message = "URL must be less than 2048 characters")
        String productUrl,

        @Size(max = 1000)
        String description
) {}
