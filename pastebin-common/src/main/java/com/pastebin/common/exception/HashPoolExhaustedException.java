package com.pastebin.common.exception;

public class HashPoolExhaustedException extends RuntimeException {
    public HashPoolExhaustedException(String message) {
        super(message);
    }
}
