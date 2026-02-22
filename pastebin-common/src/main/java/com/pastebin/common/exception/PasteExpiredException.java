package com.pastebin.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasteExpiredException extends ResponseStatusException {

    public PasteExpiredException(String hash) {
        super(HttpStatus.GONE, "Paste has expired: " + hash);
    }
}