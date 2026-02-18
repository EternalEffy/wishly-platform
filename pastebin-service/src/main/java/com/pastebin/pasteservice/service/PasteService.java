package com.pastebin.pasteservice.service;

import com.pastebin.pasteservice.model.entity.Paste;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface PasteService {
    @Transactional
    Paste createPaste(String content, Instant expiresAt);
    Paste getPasteByHash(String hash);

    @Transactional
    long deleteExpiredPastes();

    @Transactional
    boolean deletePaste(String hash);
    boolean existPaste(String hash);
}
