package com.wishly.wishlistservice.model.entity;

import com.wishly.wishlistservice.model.enums.Privacy;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wishlists", indexes = {
        @Index(name = "idx_wishlists_owner_id", columnList = "owner_id"),
        @Index(name = "idx_wishlists_privacy", columnList = "privacy"),
        @Index(name = "idx_wishlists_owner_privacy", columnList = "owner_id, privacy")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "privacy", nullable = false)
    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @Column(name = "archived", nullable = false)
    @Builder.Default
    private boolean archived = false;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("priority DESC, createdAt ASC")
    private List<GiftItem> items = new ArrayList<>();

    public void archive() {
        this.archived = true;
        this.archivedAt = LocalDateTime.now();
    }

}
