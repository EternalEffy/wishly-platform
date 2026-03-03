package com.wishly.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasteNotFoundException extends ResponseStatusException {

    public PasteNotFoundException(String hash) {
        super(HttpStatus.NOT_FOUND, "Paste not found: " + hash);
    }
}
