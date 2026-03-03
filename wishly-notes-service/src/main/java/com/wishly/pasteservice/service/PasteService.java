package com.wishly.pasteservice.service;

import com.wishly.pasteservice.model.entity.Paste;
import com.wishly.pasteservice.model.enums.Privacy;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PasteService {
    @Transactional
    Paste createPaste(String content, Instant expiresAt, UUID ownerId, Privacy privacy);

    Paste getPasteByHash(String hash, UUID ownerId);

    String getPasteContent(Paste paste);

    List<Paste> getUserPublicPastes(UUID ownerId);

    @Transactional
    boolean deletePaste(String hash, UUID ownerId);

    List<Paste> getMyPastes(UUID ownerId);

}
