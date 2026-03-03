package com.wishly.pasteservice.exception;

public class BlobStorageException extends RuntimeException {

    public BlobStorageException(String message) {
        super(message);
    }

    public BlobStorageException(String message, Throwable cause) {  // ← Throwable
        super(message, cause);
    }
}
