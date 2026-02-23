package com.pastebin.pasteservice.service.impl;

import com.pastebin.common.exception.PasteExpiredException;
import com.pastebin.common.exception.PasteNotFoundException;
import com.pastebin.common.generator.HashGenerator;
import com.pastebin.pasteservice.entity.Paste;
import com.pastebin.pasteservice.repository.PasteRepository;
import com.pastebin.pasteservice.service.PasteService;
import com.pastebin.pasteservice.service.blob.BlobStorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasteServiceImpl implements PasteService {

    private final PasteRepository pasteRepository;
    private final HashGenerator hashGenerator;
    private final BlobStorageService blobStorageService;

    private static final int HASH_LENGTH = 8;
    private static final String BLOB_KEY_PREFIX = "pastes/";
    private static final String CONTENT_TYPE = "text/plain";

    @Override
    @Transactional
    public Paste createPaste(String content, Instant expiresAt, UUID ownerId) {
        log.info("Creating new paste for user: {}, content length: {}", ownerId, content.length());

        validateExpiration(expiresAt);

        String hash = hashGenerator.generate(HASH_LENGTH);
        String blobKey = BLOB_KEY_PREFIX + hash;

        blobStorageService.store(blobKey, content);

        Paste paste = Paste.builder()
                .hash(hash)
                .blobKey(blobKey)
                .contentType(CONTENT_TYPE)
                .contentSize((long) content.length())
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .ownerId(ownerId)
                .build();

        Paste saved = pasteRepository.save(paste);
        log.info("Paste created successfully with hash: {} for user: {}", saved.getHash(), ownerId);

        return saved;
    }

    @Override
    public Paste getPasteByHash(String hash, UUID ownerId) {
        Paste paste = pasteRepository.findById(hash)
                .orElseThrow(() -> new PasteNotFoundException(hash));

        if (isExpired(paste)) {
            throw new PasteExpiredException(hash);
        }

        return paste;
    }

    @Override
    public String getPasteContent(Paste paste) {
        return blobStorageService.retrieve(paste.getBlobKey());
    }

    @Override
    @Transactional
    public boolean deletePaste(String hash, UUID ownerId) {
        Paste paste = pasteRepository.findByHashAndOwnerId(hash, ownerId)
                .orElseThrow(() -> {
                    return pasteRepository.findById(hash)
                            .map(p -> new RuntimeException("Paste does not belong to you"))
                            .orElse(new PasteNotFoundException(hash));
                });

        blobStorageService.delete(paste.getBlobKey());
        pasteRepository.deleteById(hash);

        log.info("Paste deleted: {} by user: {}", hash, ownerId);
        return true;
    }

    @Override
    public List<Paste> getMyPastes(UUID ownerId) {
        log.info("Getting all pastes for user: {}", ownerId);
        return pasteRepository.findByOwnerId(ownerId);
    }

    private void validateExpiration(Instant expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
    }

    private boolean isExpired(Paste paste) {
        return paste.getExpiresAt() != null && paste.getExpiresAt().isBefore(Instant.now());
    }
}