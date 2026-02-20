package com.pastebin.pasteservice.controller;

import com.pastebin.pasteservice.dto.CreatePasteRequest;
import com.pastebin.pasteservice.model.entity.Paste;
import com.pastebin.pasteservice.service.PasteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pastes")
public class PasteController {
    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @PostMapping
    public ResponseEntity<Paste> createPaste(@Valid @RequestBody CreatePasteRequest request) {
        Paste paste = pasteService.createPaste(request.content(), request.expiresAt());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/pastes/" + paste.getHash())
                .body(paste);
    }

    @GetMapping("/{hash:[a-zA-Z0-9]+}")
    public ResponseEntity<Paste> getPasteByHash(@PathVariable String hash){
        Paste paste = pasteService.getPasteByHash(hash);
        return ResponseEntity.ok(paste);
    }

    @DeleteMapping("/{hash:[a-zA-Z0-9]+}")
    public ResponseEntity<Void> deletePaste(@PathVariable String hash){
        if(pasteService.deletePaste(hash)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
