package com.wishly.wishlistservice.service.impl;

import com.wishly.wishlistservice.dto.ReservationRequest;
import com.wishly.wishlistservice.dto.ReservationResponse;
import com.wishly.wishlistservice.exception.GiftItemAlreadyReservedException;
import com.wishly.wishlistservice.exception.GiftItemNotFoundException;
import com.wishly.wishlistservice.exception.ReservationNotFoundException;
import com.wishly.wishlistservice.exception.WishlistNotFoundException;
import com.wishly.wishlistservice.model.entity.GiftItem;
import com.wishly.wishlistservice.model.entity.Reservation;
import com.wishly.wishlistservice.model.entity.Wishlist;
import com.wishly.wishlistservice.model.enums.Privacy;
import com.wishly.wishlistservice.repository.GiftItemRepository;
import com.wishly.wishlistservice.repository.ReservationRepository;
import com.wishly.wishlistservice.repository.WishlistRepository;
import com.wishly.wishlistservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository repository;
    private final GiftItemRepository giftItemRepository;
    private final WishlistRepository wishlistRepository;

    @Override
    public ReservationResponse reserveGift(UUID itemId, UUID wishlistId, UUID userId, ReservationRequest request) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new WishlistNotFoundException(wishlistId));

        if (wishlist.getPrivacy() == Privacy.PRIVATE && !wishlist.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Private wishlist");
        }

        GiftItem giftItem = giftItemRepository.findById(itemId)
                .orElseThrow(() -> new GiftItemNotFoundException(itemId));

        if (giftItem.isReserved()) {
            throw new GiftItemAlreadyReservedException(itemId);
        }

        if (userId == null && (request.guestEmail() == null || request.guestEmail().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guest email is required");
        }

        Reservation reservation = Reservation.builder()
                .giftItem(giftItem)
                .userId(userId)
                .guestEmail(request.guestEmail())
                .guestName(request.guestName())
                .build();


        giftItem.setReserved(true);
        giftItem.setReservedByName(request.guestName());
        giftItem.setReservedByEmail(request.guestEmail());
        giftItem.setReservedAt(LocalDateTime.now());
        Reservation saved = repository.save(reservation);
        giftItem.setActiveReservation(saved);
        giftItemRepository.save(giftItem);

        log.info("Created reservation: {} for item: {}", saved.getId(), itemId);
        return toResponse(saved);
    }

    @Override
    public void cancelReservation(UUID itemId, UUID wishlistId, UUID userId, String guestEmail) {
        log.info("=== cancelReservation START ===");
        Reservation reservation = repository.findByGiftItemIdAndStatus(itemId, Reservation.ReservationStatus.ACTIVE)
                .orElseThrow(() -> new ReservationNotFoundException(itemId));

        if (userId != null && !userId.equals(reservation.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your reservation");
        }
        if (userId == null && !guestEmail.equals(reservation.getGuestEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your reservation");
        }

        reservation.cancel();
        repository.save(reservation);
        log.info("Reservation cancelled: {}", reservation.getId());

        GiftItem giftItem = giftItemRepository.findById(itemId)
                .orElseThrow(() -> new GiftItemNotFoundException(itemId));

        log.info("Before: reserved={}, name={}", giftItem.isReserved(), giftItem.getReservedByName());

        giftItem.setReserved(false);
        giftItem.setReservedByName(null);
        giftItem.setReservedByEmail(null);
        giftItem.setReservedAt(null);
        giftItem.setActiveReservation(null);
        GiftItem saved = giftItemRepository.save(giftItem);
        log.info("After save: saved.reserved={}, id={}", saved.isReserved(), saved.getId());

        log.info("Cancelled reservation: {} for item: {}", reservation.getId(), itemId);
    }

    @Override
    public List<ReservationResponse> getMyReservations(UUID userId) {
        List<Reservation> reservations = repository.findAllByUserIdAndStatus(userId, Reservation.ReservationStatus.ACTIVE);
        return reservations.stream()
                .map(this::toResponse)
                .toList();
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    @Override
    public int expireReservations() {
        List<Reservation> expired = repository.findAllByStatusAndExpiresAtBefore(
                Reservation.ReservationStatus.ACTIVE,
                LocalDateTime.now()
        );

        for (Reservation r : expired) {
            r.expire();
        }

        log.info("Expired {} reservations", expired.size());
        return expired.size();
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getExpiresAt(),
                reservation.getGuestName()
        );
    }
}
