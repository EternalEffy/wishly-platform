package com.pastebin.authservice.dto;

import java.util.UUID;

public record AuthResponse(
        TokenResponse tokens,
        String email,
        UUID id
) {

}
