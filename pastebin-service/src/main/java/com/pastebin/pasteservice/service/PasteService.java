package com.pastebin.pasteservice.service;

import com.pastebin.pasteservice.entity.Paste;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface PasteService {
    @Transactional
    Paste createPaste(String content, Instant expiresAt);

    Paste getPasteByHash(String hash);

    String getPasteContent(Paste paste);

    @Transactional
    boolean deletePaste(String hash);

}
