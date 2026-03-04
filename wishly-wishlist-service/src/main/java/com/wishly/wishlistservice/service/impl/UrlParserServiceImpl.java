package com.wishly.wishlistservice.service.impl;

import com.wishly.wishlistservice.dto.ProductMetadata;
import com.wishly.wishlistservice.exception.UrlNotSupportedException;
import com.wishly.wishlistservice.service.SupportedSitesService;
import com.wishly.wishlistservice.service.UrlParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlParserServiceImpl implements UrlParserService {
    private final SupportedSitesService supportedSitesService;
    @Override
    public ProductMetadata parseUrl(String productUrl) {
        String trimmedUrl = productUrl.trim();
        log.info("Parsing URL (stub): {}", trimmedUrl);

        if (!supportedSitesService.isSupported(trimmedUrl)) {
            throw new UrlNotSupportedException(trimmedUrl);
        }

        String siteName = supportedSitesService.getSiteName(trimmedUrl);

        return new ProductMetadata(
                trimmedUrl,
                "Laptop from " + siteName,
                "Features\n" +
                        "Processor — Intel Celeron N5095\n" +
                        "Screen diagonal (inches) — 15.6\"\n" +
                        "Screen resolution — 2256x1544\n" +
                        "Video card type — Built-in\n" +
                        "Video card — Intel UHD Graphics",
                "https://ezzzbox.ru/upload/iblock/834/q9zlxld8ndj7h6e2daixrru2n91gsyne.jpg",
                new BigDecimal("24990"),
                "RUB",
                siteName,
                UUID.randomUUID().toString()
        );
    }

    @Override
    public boolean isSupported(String url) {
        return supportedSitesService.isSupported(url);
    }

    @Override
    public String getSiteName(String url) {
        return supportedSitesService.getSiteName(url);
    }
}
