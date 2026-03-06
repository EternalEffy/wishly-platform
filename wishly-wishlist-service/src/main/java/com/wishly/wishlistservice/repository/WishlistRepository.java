package com.wishly.wishlistservice.repository;

import com.wishly.wishlistservice.model.entity.Wishlist;
import com.wishly.wishlistservice.model.enums.Privacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {

    List<Wishlist> findAllByOwnerId(UUID ownerId);

    Optional<Wishlist> findByIdAndOwnerId(UUID id,UUID ownerId);

    boolean existsByIdAndOwnerId(UUID id, UUID ownerId);

    List<Wishlist> findAllByOwnerIdAndPrivacy(UUID ownerId, Privacy privacy);
    List<Wishlist> findAllByEventDateBeforeAndArchived(LocalDateTime date, boolean archived);
}
