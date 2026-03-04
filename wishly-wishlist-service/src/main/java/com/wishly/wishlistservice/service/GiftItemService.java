package com.wishly.wishlistservice.service;

import com.wishly.wishlistservice.dto.GiftItemRequest;
import com.wishly.wishlistservice.dto.GiftItemResponse;

import java.util.List;
import java.util.UUID;

public interface GiftItemService {
    GiftItemResponse createGiftItem(UUID wishlistId, GiftItemRequest request, UUID ownerId);

    List<GiftItemResponse> getGiftItems(UUID wishlistId, UUID ownerId);

    void deleteGiftItem(UUID wishlistId, UUID itemId, UUID ownerId);
}
