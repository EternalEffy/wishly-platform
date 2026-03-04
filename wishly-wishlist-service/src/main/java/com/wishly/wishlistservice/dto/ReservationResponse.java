package com.wishly.wishlistservice.dto;

import com.wishly.wishlistservice.model.entity.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        Reservation.ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        String guestName
        ) {
}
