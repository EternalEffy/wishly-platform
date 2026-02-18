package com.pastebin.common.exception;

public class PasteNotFoundException extends RuntimeException {
    public PasteNotFoundException(String hash) {
        super("Paste not found: " + hash);
    }
}
