package com.wishly.wishlistservice.exception;

import java.util.UUID;

public class WishlistNotFoundException extends RuntimeException {
  public WishlistNotFoundException(UUID id) {
    super("Wishlist not found with id: " + id);
  }
}
