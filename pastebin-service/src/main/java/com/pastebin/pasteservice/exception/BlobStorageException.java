package com.pastebin.pasteservice.exception;

import lombok.Getter;

public class BlobStorageException extends RuntimeException {

    public BlobStorageException(String message) {
        super(message);
    }

    public BlobStorageException(String message, Throwable cause) {  // ← Throwable
        super(message, cause);
    }
}
