package com.wishly.wishlistservice.exception;

import java.util.UUID;

public class GiftItemNotFoundException extends RuntimeException {
    public GiftItemNotFoundException(UUID id) {
        super("Gift item not found with id: " + id);
    }
}
