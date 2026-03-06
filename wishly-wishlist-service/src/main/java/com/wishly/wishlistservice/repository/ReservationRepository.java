package com.wishly.wishlistservice.repository;

import com.wishly.wishlistservice.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByGiftItemIdAndStatus(UUID giftItemId, Reservation.ReservationStatus status);

    List<Reservation> findAllByUserIdAndStatus(UUID userId, Reservation.ReservationStatus status);

    List<Reservation> findAllByStatusAndExpiresAtBefore(Reservation.ReservationStatus status, LocalDateTime date);

    long countByUserIdAndStatus(UUID userId, Reservation.ReservationStatus status);
}
