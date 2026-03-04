package com.wishly.wishlistservice.service;

import com.wishly.wishlistservice.dto.ProductMetadata;

public interface UrlParserService {
    ProductMetadata parseUrl(String productUrl);

    boolean isSupported(String url);

    String getSiteName(String url);

}
