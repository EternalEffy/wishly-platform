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


@RestController
@RequestMapping("/api/pastes")
@RequiredArgsConstructor
public class PasteController {

    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<Paste> createPaste(@Validated @RequestBody CreatePasteRequest request) {
        Paste paste = pasteService.createPaste(request.content(), request.expiresAt());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/pastes/" + paste.getHash())
                .body(paste);
    }

    @GetMapping("/{hash:[a-zA-Z0-9]+}")
    public ResponseEntity<PasteResponse> getPasteByHash(@PathVariable String hash) {
        Paste paste = pasteService.getPasteByHash(hash);
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
    public ResponseEntity<Void> deletePaste(@PathVariable String hash) {
        if (pasteService.deletePaste(hash)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
