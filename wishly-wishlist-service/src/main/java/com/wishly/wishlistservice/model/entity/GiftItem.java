package com.wishly.wishlistservice.model.entity;

import com.wishly.wishlistservice.model.enums.PriorityLevel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "gift_items", indexes = {
        @Index(name = "idx_gift_items_wishlist_id", columnList = "wishlist_id"),
        @Index(name = "idx_gift_items_url_hash", columnList = "url_hash"),
        @Index(name = "idx_gift_items_purchased", columnList = "purchased")
})
@EntityListeners(AuditingEntityListener.class)
public class GiftItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PriorityLevel priority = PriorityLevel.MEDIUM;

    @OneToOne(mappedBy = "giftItem", fetch = FetchType.LAZY)
    private Reservation activeReservation;

    @Column(name = "product_url")
    private String productUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "url_hash")
    private String urlHash;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public boolean isReserved() {
        return activeReservation != null && activeReservation.isActive();
    }

}
