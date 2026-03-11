package com.wishly.wishlistservice.controller;

import com.wishly.wishlistservice.dto.*;
import com.wishly.wishlistservice.service.GiftItemService;
import com.wishly.wishlistservice.service.ReservationService;
import com.wishly.wishlistservice.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {
    private final WishlistService wishlistService;
    private final GiftItemService giftItemService;
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<WishlistResponse> createWishlist(
            @Valid @RequestBody WishlistRequest request,
            @RequestHeader("X-User-Id") UUID ownerId) {
        log.info("Creating wishlist for user:{}", ownerId);
        WishlistResponse response = wishlistService.createWishlist(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<WishlistResponse>> getMyWishlists(
            @RequestHeader("X-User-Id") UUID ownerId) {
        log.info("Getting wishlists for user:{}", ownerId);
        List<WishlistResponse> responses = wishlistService.getMyWishlists(ownerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistResponse> getWishlist(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID ownerId) {

        log.info("Getting wishlist: {} for user: {}", id, ownerId);
        WishlistResponse response = wishlistService.getWishlist(id, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<WishlistResponse> getPublicWishlist(
            @PathVariable UUID id) {

        log.info("Getting public wishlist: {}", id);
        WishlistResponse response = wishlistService.getPublicWishlist(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WishlistResponse> updateWishlist(
            @PathVariable UUID id,
            @Valid @RequestBody WishlistRequest request,
            @RequestHeader("X-User-Id") UUID ownerId) {

        log.info("Updating wishlist: {} for user: {}", id, ownerId);
        WishlistResponse response = wishlistService.updateWishlist(id, request, ownerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID ownerId) {

        log.info("Deleting wishlist: {} for user: {}", id, ownerId);
        wishlistService.deleteWishlist(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    // === GiftItem Endpoints ===

    @PostMapping("/{wishlistId}/items")
    public ResponseEntity<GiftItemResponse> createGiftItem(
            @PathVariable UUID wishlistId,
            @Valid @RequestBody GiftItemRequest request,
            @RequestHeader("X-User-Id") UUID ownerId) {

        log.info("Creating gift item for wishlist: {}", wishlistId);
        GiftItemResponse response = giftItemService.createGiftItem(wishlistId, request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{wishlistId}/items")
    public ResponseEntity<List<GiftItemResponse>> getGiftItems(
            @PathVariable UUID wishlistId,
            @RequestHeader("X-User-Id") UUID ownerId) {

        log.info("Getting gift items for wishlist: {}", wishlistId);
        List<GiftItemResponse> responses = giftItemService.getGiftItems(wishlistId, ownerId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{wishlistId}/items/{itemId}")
    public ResponseEntity<Void> deleteGiftItem(
            @PathVariable UUID wishlistId,
            @PathVariable UUID itemId,
            @RequestHeader("X-User-Id") UUID ownerId) {

        log.info("Deleting gift item: {} from wishlist: {}", itemId, wishlistId);
        giftItemService.deleteGiftItem(wishlistId, itemId, ownerId);
        return ResponseEntity.noContent().build();
    }

    // === Reservation Endpoints ===

    @PostMapping("/{wishlistId}/items/{itemId}/reserve")
    public ResponseEntity<ReservationResponse> reserveGift(
            @PathVariable UUID wishlistId,
            @PathVariable UUID itemId,
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {

        log.info("Reserving gift item: {} by user: {}", itemId, userId);
        ReservationResponse response = reservationService.reserveGift(itemId, wishlistId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{wishlistId}/items/{itemId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable UUID wishlistId,
            @PathVariable UUID itemId,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestParam(required = false) String guestEmail) {

        log.info("Cancelling reservation for gift item: {}", itemId);
        reservationService.cancelReservation(itemId, wishlistId, userId, guestEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Getting reservations for user: {}", userId);
        List<ReservationResponse> responses = reservationService.getMyReservations(userId);
        return ResponseEntity.ok(responses);
    }
}
