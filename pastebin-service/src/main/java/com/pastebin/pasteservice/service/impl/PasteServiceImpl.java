package com.pastebin.pasteservice.service.impl;

import com.pastebin.common.exception.PasteExpiredException;
import com.pastebin.common.exception.PasteNotFoundException;
import com.pastebin.common.generator.HashGenerator;
import com.pastebin.pasteservice.model.entity.Paste;
import com.pastebin.pasteservice.repository.PasteRepository;
import com.pastebin.pasteservice.service.PasteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PasteServiceImpl implements PasteService {
    private final PasteRepository pasteRepository;
    private final HashGenerator hashGenerator;
    private static final int HASH_LENGTH = 8;

    public PasteServiceImpl(PasteRepository pasteRepository, HashGenerator hashGenerator) {
        this.pasteRepository = pasteRepository;
        this.hashGenerator = hashGenerator;
    }

    @Override
    @Transactional
    public Paste createPaste(String content, Instant expiresAt) {
        log.info("Creating new paste, content length: {}", content.length());

        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
        String hash = hashGenerator.generate(HASH_LENGTH);
        log.info("Generated hash: {}", hash);

        Paste paste = Paste.builder()
                .hash(hash)
                .content(content)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .build();
        Paste saved = pasteRepository.save(paste);
        log.info("Paste created successfully with hash: {}", saved.getHash());

        return saved;
    }

    @Override
    public Paste getPasteByHash(String hash) {
        Paste paste = pasteRepository.findById(hash)
                .orElseThrow(() -> new PasteNotFoundException(hash));
        if (paste.getExpiresAt() != null && paste.getExpiresAt().isBefore(Instant.now())) {
            throw new PasteExpiredException(hash);
        }
        return paste;
    }

    @Transactional
    @Override
    public long deleteExpiredPastes() {
        return pasteRepository.deleteByExpiresAtBefore(Instant.now());
    }

    @Override
    @Transactional
    public boolean deletePaste(String hash) {
        if (!pasteRepository.existsById(hash)) {
            return false;
        }
        pasteRepository.deleteById(hash);
        return true;
    }

    @Override
    public boolean existPaste(String hash) {
        return pasteRepository.existsById(hash);
    }
}
