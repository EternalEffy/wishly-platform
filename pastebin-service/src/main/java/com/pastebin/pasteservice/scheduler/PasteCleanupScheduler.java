package com.pastebin.pasteservice.scheduler;

import com.pastebin.pasteservice.model.entity.Paste;
import com.pastebin.pasteservice.repository.PasteRepository;
import com.pastebin.pasteservice.service.blob.BlobStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PasteCleanupScheduler {

    private final PasteRepository pasteRepository;
    private final BlobStorageService blobStorageService;

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredPastes() {
        log.info("Starting expired pastes cleanup");

        List<Paste> expired = pasteRepository.findByExpiresAtBefore(Instant.now());

        for (Paste paste : expired) {
            blobStorageService.delete(paste.getBlobKey());
        }

        long deleted = pasteRepository.deleteByExpiresAtBefore(Instant.now());
        log.info("Cleanup completed: {} pastes deleted", deleted);
    }
}
