package com.pastebin.pasteservice.controller;

import com.pastebin.pasteservice.dto.CreatePasteRequest;
import com.pastebin.pasteservice.dto.PasteResponse;
import com.pastebin.pasteservice.entity.Paste;
import com.pastebin.pasteservice.service.PasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/pastes")
@RequiredArgsConstructor
public class PasteController {

    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<Paste> createPaste(
            @Validated @RequestBody CreatePasteRequest request,
            @RequestHeader("X-User-Id") UUID ownerId) {
        Paste paste = pasteService.createPaste(request.content(), request.expiresAt(), ownerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/pastes/" + paste.getHash())
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
}
