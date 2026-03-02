package com.pastebin.pasteservice.model.entity;

import com.pastebin.pasteservice.model.enums.Privacy;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pastes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Paste {
    @Id
    @NotBlank
    @Column(nullable = false, length = 50)
    private String hash;

    @Column(nullable = false,name = "privacy")
    @Enumerated(EnumType.STRING)
    private Privacy privacy = Privacy.UNLISTED;

    @Column(name = "blob_key", nullable = false)
    private String blobKey;

    @Column(name = "content_type")
    private String contentType = "text/plain";

    @Column(name = "content_size")
    private Long contentSize;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @NotNull
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    private Instant expiresAt;

    @Version
    private Long version;
}
