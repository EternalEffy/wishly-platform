package com.pastebin.pasteservice.repository;

import com.pastebin.pasteservice.model.entity.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;


@Repository
public interface PasteRepository extends JpaRepository<Paste, String> {
    long deleteByExpiresAtBefore(Instant now);
}
