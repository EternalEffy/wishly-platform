package com.pastebin.pasteservice.repository;

import com.pastebin.pasteservice.entity.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
public interface PasteRepository extends JpaRepository<Paste, String> {
    List<Paste> findByExpiresAtBefore(Instant instant);
    long deleteByExpiresAtBefore(Instant now);
}
