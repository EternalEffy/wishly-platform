package com.wishly.wishlistservice.service;

import com.wishly.wishlistservice.dto.WishlistRequest;
import com.wishly.wishlistservice.dto.WishlistResponse;
import com.wishly.wishlistservice.model.entity.Wishlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistService {
    WishlistResponse createWishlist(WishlistRequest request, UUID ownerId);

    WishlistResponse getWishlist(UUID id, UUID ownerId);

    List<WishlistResponse> getMyWishlists(UUID ownerId);

    WishlistResponse updateWishlist(UUID id, WishlistRequest request, UUID ownerId);

    void deleteWishlist(UUID id, UUID ownerId);

    WishlistResponse getPublicWishlist(UUID id);
}
