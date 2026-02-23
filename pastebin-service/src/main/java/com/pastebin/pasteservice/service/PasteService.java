package com.pastebin.pasteservice.service;

import com.pastebin.pasteservice.entity.Paste;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PasteService {
    @Transactional
    Paste createPaste(String content, Instant expiresAt, UUID ownerId);

    Paste getPasteByHash(String hash, UUID ownerId);

    String getPasteContent(Paste paste);

    @Transactional
    boolean deletePaste(String hash, UUID ownerId);

    List<Paste> getMyPastes(UUID ownerId);

}
