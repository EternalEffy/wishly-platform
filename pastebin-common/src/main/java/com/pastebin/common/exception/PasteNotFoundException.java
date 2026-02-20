package com.pastebin.common.exception;

import lombok.Getter;

@Getter
public class PasteNotFoundException extends RuntimeException {
    private final String hash;

    public PasteNotFoundException(String hash) {
        super("Paste not found: " + hash);
        this.hash = hash;
    }
}
