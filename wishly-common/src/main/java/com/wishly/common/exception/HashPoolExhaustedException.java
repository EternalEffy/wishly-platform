package com.wishly.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HashPoolExhaustedException extends ResponseStatusException {

    public HashPoolExhaustedException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, "Hash pool exhausted. Please retry.");
    }
}