package com.wishly.wishlistservice.model.entity;

import com.wishly.wishlistservice.model.enums.PriorityLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
        @Index(name = "idx_gift_items_url_hash", columnList = "url_hash")
})
@EntityListeners(AuditingEntityListener.class)
public class GiftItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wishlist wishlist;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "product_url", length = 2048)
    private String productUrl;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    @Builder.Default
    private PriorityLevel priority = PriorityLevel.MEDIUM;

    @Column(name = "reserved", nullable = false)
    @Builder.Default
    private boolean reserved = false;

    @Column(name = "reserved_by_name", length = 255)
    private String reservedByName;

    @Column(name = "reserved_by_email", length = 255)
    private String reservedByEmail;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "active_reservation_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reservation activeReservation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
