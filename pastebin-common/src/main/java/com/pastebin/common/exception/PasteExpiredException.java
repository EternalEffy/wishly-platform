package com.pastebin.common.exception;

public class PasteExpiredException extends RuntimeException {
    public PasteExpiredException(String hash) {
        super("Paste expired: " + hash);
    }
}