package com.wishly.wishlistservice.service.impl;

import com.wishly.wishlistservice.dto.WishlistRequest;
import com.wishly.wishlistservice.dto.WishlistResponse;
import com.wishly.wishlistservice.exception.WishlistNotFoundException;
import com.wishly.wishlistservice.model.entity.Wishlist;
import com.wishly.wishlistservice.model.enums.Privacy;
import com.wishly.wishlistservice.repository.WishlistRepository;
import com.wishly.wishlistservice.service.WishlistService;
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
@Transactional
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository repository;

    @Override
    public WishlistResponse createWishlist(WishlistRequest request, UUID ownerId) {
        Wishlist wishlist = Wishlist.builder()
                .title(request.title())
                .ownerId(ownerId)
                .privacy(request.privacy())
                .eventDate(request.eventDate())
                .archived(false)
                .build();
        Wishlist saved = repository.save(wishlist);
        log.info("Created wishlist: {} for user: {}", saved.getId(), ownerId);
        return toResponse(saved);
    }

    @Override
    public WishlistResponse getWishlist(UUID id, UUID ownerId) {
        Wishlist wishlist = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new WishlistNotFoundException(id));
        if (wishlist.isArchived()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist is archived");
        }
        return toResponse(wishlist);
    }

    @Override
    public List<WishlistResponse> getMyWishlists(UUID ownerId) {
        List<Wishlist> wishlists = repository.findAllByOwnerId(ownerId);
        return wishlists.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public WishlistResponse updateWishlist(UUID id, WishlistRequest request, UUID ownerId) {
        Wishlist wishlist = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new WishlistNotFoundException(id));
        if (wishlist.isArchived()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist is archived");
        }
        wishlist.setTitle(request.title());
        wishlist.setPrivacy(request.privacy());
        wishlist.setEventDate(request.eventDate());

        Wishlist updated = repository.save(wishlist);
        log.info("Updated wishlist: {} for user: {}", updated.getId(), ownerId);
        return toResponse(updated);
    }

    @Override
    public void deleteWishlist(UUID id, UUID ownerId) {
        Wishlist wishlist = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new WishlistNotFoundException(id));
        repository.delete(wishlist);
        log.info("Deleted wishlist: {} for user: {}", id, ownerId);
    }

    @Override
    public WishlistResponse getPublicWishlist(UUID id) {
        Wishlist wishlist = repository.findById(id)
                .orElseThrow(() -> new WishlistNotFoundException(id));
        if (wishlist.getPrivacy() != Privacy.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wishlist is private");
        }
        return toResponse(wishlist);
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getTitle(),
                wishlist.getOwnerId(),
                wishlist.getPrivacy(),
                wishlist.getEventDate(),
                wishlist.getCreatedAt(),
                wishlist.getItems().size(),
                wishlist.isArchived()
        );
    }
}
