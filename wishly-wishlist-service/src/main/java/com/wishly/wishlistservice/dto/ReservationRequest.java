package com.wishly.wishlistservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ReservationRequest(

        @Email(message = "Invalid email format")
        String guestEmail,

        @Size(max = 100, message = "Name too long")
        String guestName
) {
}
