package com.wishly.wishlistservice.exception;

public class UrlNotSupportedException extends RuntimeException {
  public UrlNotSupportedException(String url) {
    super("URL not supported: " + url);
  }
}
