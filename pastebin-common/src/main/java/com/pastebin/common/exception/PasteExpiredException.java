package com.pastebin.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.GONE)  // 410 Gone
public class PasteExpiredException extends RuntimeException {
    private final String hash;

    public PasteExpiredException(String hash) {
        super("Paste has expired: " + hash);
        this.hash = hash;
    }
}