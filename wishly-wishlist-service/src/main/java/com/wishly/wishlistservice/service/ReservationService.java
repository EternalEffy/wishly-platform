package com.wishly.wishlistservice.service;

import com.wishly.wishlistservice.dto.ReservationRequest;
import com.wishly.wishlistservice.dto.ReservationResponse;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
    ReservationResponse reserveGift(UUID itemId, UUID wishlistId, UUID userId, ReservationRequest request);

    void cancelReservation(UUID itemId, UUID wishlistId, UUID userId,String guestEmail);

    List<ReservationResponse> getMyReservations(UUID userId);

    @Scheduled(cron = "0 0 * * * *")
    int expireReservations();
}
