package com.wishly.wishlistservice.exception;

import java.util.UUID;

public class GiftItemAlreadyReservedException extends RuntimeException {
  public GiftItemAlreadyReservedException(UUID itemId) {
    super("Gift item already reserved: " + itemId);
  }
}
