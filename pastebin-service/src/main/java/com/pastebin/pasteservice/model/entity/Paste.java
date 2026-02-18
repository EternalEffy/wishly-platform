package com.pastebin.pasteservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "pastes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paste {
    @Id
    @NotBlank
    @Column (nullable = false,length = 50)
    private String hash;

    @NotBlank
    @Size(min = 1,max = 10000)
    @Column (nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    private Instant expiresAt;

    @Version
    private Long version;
}
