package com.pastebin.pasteservice.exception;

public class HashGenerationException extends RuntimeException {
    public HashGenerationException(String message) {
        super(message);
    }

    public HashGenerationException(String message, Exception e) {
        super(message, e);
    }
}
