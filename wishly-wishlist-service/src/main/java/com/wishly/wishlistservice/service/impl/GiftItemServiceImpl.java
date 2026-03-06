package com.wishly.wishlistservice.service.impl;

import com.wishly.wishlistservice.dto.GiftItemRequest;
import com.wishly.wishlistservice.dto.GiftItemResponse;
import com.wishly.wishlistservice.exception.GiftItemNotFoundException;
import com.wishly.wishlistservice.exception.WishlistNotFoundException;
import com.wishly.wishlistservice.model.entity.GiftItem;
import com.wishly.wishlistservice.model.entity.Wishlist;
import com.wishly.wishlistservice.model.enums.PriorityLevel;
import com.wishly.wishlistservice.repository.GiftItemRepository;
import com.wishly.wishlistservice.repository.WishlistRepository;
import com.wishly.wishlistservice.service.GiftItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GiftItemServiceImpl implements GiftItemService {
    private final GiftItemRepository giftRepository;
    private final WishlistRepository wishlistRepository;

    @Override
    @Transactional
    public GiftItemResponse createGiftItem(UUID wishlistId, GiftItemRequest request,UUID ownerId) {
        log.info("Creating gift item for wishlist: {}", wishlistId);

        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new WishlistNotFoundException(wishlistId));

        String validatedUrl = null;
        if (request.productUrl() != null && !request.productUrl().isBlank()) {
            validatedUrl = validateUrl(request.productUrl());
        }

        GiftItem giftItem = GiftItem.builder()
                .wishlist(wishlist)
                .name(request.name())
                .productUrl(validatedUrl)
                .description(request.description())
                .priority(request.priority() != null ? request.priority() : PriorityLevel.MEDIUM)
                .build();

        giftRepository.save(giftItem);

        log.info("Created gift item: {}", giftItem.getId());

        return toResponse(giftItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftItemResponse> getGiftItems(UUID wishlistId, UUID ownerId) {
        Wishlist wishlist = wishlistRepository.findByIdAndOwnerId(wishlistId, ownerId)
                .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        List<GiftItem> items = giftRepository.findAllByWishlistId(wishlistId);
        return items.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteGiftItem(UUID wishlistId, UUID itemId, UUID ownerId) {
        Wishlist wishlist = wishlistRepository.findByIdAndOwnerId(wishlistId, ownerId)
                .orElseThrow(() -> new WishlistNotFoundException(wishlistId));

        GiftItem giftItem = giftRepository.findByIdAndWishlistId(itemId, wishlistId)
                .orElseThrow(() -> new GiftItemNotFoundException(itemId));

        giftRepository.delete(giftItem);

        log.info("Deleted gift item: {} from wishlist: {}", itemId, wishlistId);
    }

    private String validateUrl(String productUrl) {
        String trimmedUrl = productUrl.trim();

        try {
            URI url = new URI(trimmedUrl);

            String host = url.getHost();
            if (host == null || host.isEmpty()) {
                throw new IllegalArgumentException("URL must have a valid host");
            }

            return trimmedUrl;

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format", e);
        }
    }

    private GiftItemResponse toResponse(GiftItem giftItem) {
        return new GiftItemResponse(
                giftItem.getId(),
                giftItem.getName(),
                giftItem.getPriority(),
                giftItem.getProductUrl(),
                giftItem.getDescription(),
                giftItem.isReserved(),
                giftItem.getReservedByName(),
                giftItem.getReservedByEmail(),
                giftItem.getReservedAt(),
                giftItem.getCreatedAt()
        );
    }
}
