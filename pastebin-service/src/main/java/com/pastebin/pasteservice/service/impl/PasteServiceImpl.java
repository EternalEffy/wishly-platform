package com.pastebin.pasteservice.service.impl;

import com.pastebin.common.exception.PasteExpiredException;
import com.pastebin.common.exception.PasteNotFoundException;
import com.pastebin.pasteservice.generator.LocalHashGenerator;
import com.pastebin.pasteservice.model.entity.Paste;
import com.pastebin.pasteservice.repository.PasteRepository;
import com.pastebin.pasteservice.service.PasteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class PasteServiceImpl implements PasteService {
    private final PasteRepository pasteRepository;
    private final LocalHashGenerator hashGenerator;

    public PasteServiceImpl(PasteRepository pasteRepository, LocalHashGenerator hashGenerator) {
        this.pasteRepository = pasteRepository;
        this.hashGenerator = hashGenerator;
    }

    @Override
    @Transactional
    public Paste createPaste(String content, Instant expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
        String hash = hashGenerator.generate(8);//временный локальный генератор хеша (позже пул в redis)
        Paste paste = Paste.builder()
                .hash(hash)
                .content(content)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .build();
        return pasteRepository.save(paste);
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
