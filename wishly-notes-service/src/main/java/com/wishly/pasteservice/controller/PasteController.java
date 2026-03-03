package com.wishly.pasteservice.controller;

import com.wishly.pasteservice.dto.CreatePasteRequest;
import com.wishly.pasteservice.dto.PasteResponse;
import com.wishly.pasteservice.model.entity.Paste;
import com.wishly.pasteservice.service.PasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/notes"})
@RequiredArgsConstructor
public class PasteController {

    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<Paste> createPaste(
            @Validated @RequestBody CreatePasteRequest request,
            @RequestHeader("X-User-Id") UUID ownerId) {
        Paste paste = pasteService.createPaste(request.content(), request.expiresAt(), ownerId, request.privacy());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/notes/" + paste.getHash())
                .body(paste);
    }

    @GetMapping("/{hash:[a-zA-Z0-9]+}")
    public ResponseEntity<PasteResponse> getPasteByHash(
            @PathVariable String hash,
            @RequestHeader(value = "X-User-Id", required = false) UUID ownerId) {
        Paste paste = pasteService.getPasteByHash(hash, ownerId);
        String content = pasteService.getPasteContent(paste);

        PasteResponse response = new PasteResponse(
                paste.getHash(),
                content,
                paste.getCreatedAt(),
                paste.getExpiresAt()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{hash:[a-zA-Z0-9]+}")
    public ResponseEntity<Void> deletePaste(
            @PathVariable String hash,
            @RequestHeader("X-User-Id") UUID ownerId) {
        if (pasteService.deletePaste(hash, ownerId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<PasteResponse>> getMyPastes(
            @RequestHeader("X-User-Id") UUID ownerId) {
        List<Paste> pastes = pasteService.getMyPastes(ownerId);

        List<PasteResponse> responses = pastes.stream()
                .map(paste -> {
                    String content = pasteService.getPasteContent(paste);
                    return new PasteResponse(
                            paste.getHash(),
                            content,
                            paste.getCreatedAt(),
                            paste.getExpiresAt()
                    );
                })
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PasteResponse>> getUserPastes(
            @PathVariable UUID userId) {

        List<Paste> pastes = pasteService.getUserPublicPastes(userId);

        List<PasteResponse> responses = pastes.stream()
                .map(paste -> new PasteResponse(
                        paste.getHash(),
                        null,
                        paste.getCreatedAt(),
                        paste.getExpiresAt()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }
}