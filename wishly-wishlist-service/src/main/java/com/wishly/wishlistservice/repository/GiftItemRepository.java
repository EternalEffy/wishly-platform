package com.wishly.wishlistservice.repository;

import com.wishly.wishlistservice.model.entity.GiftItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiftItemRepository extends JpaRepository<GiftItem, UUID> {
    List<GiftItem> findAllByWishlistId(UUID wishlistId);

    Optional<GiftItem> findByIdAndWishlistId(UUID id, UUID wishlistId);
}
