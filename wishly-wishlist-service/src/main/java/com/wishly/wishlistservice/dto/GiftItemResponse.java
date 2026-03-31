package com.wishly.wishlistservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wishly.wishlistservice.model.enums.PriorityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record GiftItemResponse(

        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("priority") PriorityLevel priority,
        @JsonProperty("productUrl") String productUrl,
        @JsonProperty("description") String description,

        @JsonProperty("reserved") boolean reserved,

        @JsonProperty("reservedByName") String reservedByName,
        @JsonProperty("reservedByEmail") String reservedByEmail,
        @JsonProperty("reservedAt") LocalDateTime reservedAt,
        @JsonProperty("createdAt") LocalDateTime createdAt

) {}
