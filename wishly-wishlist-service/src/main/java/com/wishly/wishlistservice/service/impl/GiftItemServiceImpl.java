package com.wishly.wishlistservice.service.impl;

import com.wishly.wishlistservice.dto.GiftItemRequest;
import com.wishly.wishlistservice.dto.GiftItemResponse;
import com.wishly.wishlistservice.dto.ProductMetadata;
import com.wishly.wishlistservice.exception.GiftItemNotFoundException;
import com.wishly.wishlistservice.exception.WishlistNotFoundException;
import com.wishly.wishlistservice.model.entity.GiftItem;
import com.wishly.wishlistservice.model.entity.Wishlist;
import com.wishly.wishlistservice.repository.GiftItemRepository;
import com.wishly.wishlistservice.repository.WishlistRepository;
import com.wishly.wishlistservice.service.GiftItemService;
import com.wishly.wishlistservice.service.UrlParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GiftItemServiceImpl implements GiftItemService {
    private final GiftItemRepository giftRepository;
    private final WishlistRepository wishlistRepository;
    private final UrlParserService urlParserService;

    @Override
    public GiftItemResponse createGiftItem(UUID wishlistId, GiftItemRequest request, UUID ownerId) {
        Wishlist wishlist = wishlistRepository.findByIdAndOwnerId(wishlistId, ownerId)
                .orElseThrow(() -> new WishlistNotFoundException(wishlistId));
        if (wishlist.isArchived()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist is archived");
        }
        ProductMetadata metadata = urlParserService.parseUrl(request.productUrl());

        GiftItem giftItem = GiftItem.builder()
                .wishlist(wishlist)
                .name(request.name())
                .priority(request.priority())
                .productUrl(request.productUrl())
                .imageUrl(metadata.imageUrl())
                .price(metadata.price())
                .currency(metadata.currency())
                .siteName(metadata.siteName())
                .description(metadata.description())
                .urlHash(metadata.urlHash())
                .build();
        GiftItem saved = giftRepository.save(giftItem);
        log.info("Created gift item:{} for wishlist:{}", saved.getId(), wishlistId);
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

    private GiftItemResponse toResponse(GiftItem giftItem) {
        return new GiftItemResponse(
                giftItem.getId(),
                giftItem.getName(),
                giftItem.getPriority(),
                giftItem.isReserved(),
                giftItem.getProductUrl(),
                giftItem.getImageUrl(),
                giftItem.getPrice(),
                giftItem.getCurrency(),
                giftItem.getSiteName(),
                giftItem.getDescription()
        );
    }
}
