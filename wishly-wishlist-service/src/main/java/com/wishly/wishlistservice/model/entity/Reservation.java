package com.wishly.wishlistservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservations_gift_item_id", columnList = "gift_item_id"),
        @Index(name = "idx_reservations_status", columnList = "status"),
        @Index(name = "idx_reservations_expires_at", columnList = "expires_at"),
        @Index(name = "idx_reservations_user_id", columnList = "user_id")
})
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_item_id", nullable = false)
    private GiftItem giftItem;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private LocalDateTime reservedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    public void prePersist() {
        this.reservedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(7);
        if (this.status == null) {
            this.status = ReservationStatus.ACTIVE;
        }
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE && !isExpired();
    }

    public enum ReservationStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED
    }
}
