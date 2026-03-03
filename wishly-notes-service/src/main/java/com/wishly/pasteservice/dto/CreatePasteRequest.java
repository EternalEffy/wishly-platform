package com.wishly.pasteservice.dto;

import com.wishly.pasteservice.model.enums.Privacy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreatePasteRequest(
        @NotBlank
        @Size(min = 1, max = 10000, message = "Content must be 1-10000 characters")
        String content,

        Instant expiresAt,

        Privacy privacy
) {

    public CreatePasteRequest {
        if (privacy == null) {
            privacy = Privacy.UNLISTED;
        }
    }

}
