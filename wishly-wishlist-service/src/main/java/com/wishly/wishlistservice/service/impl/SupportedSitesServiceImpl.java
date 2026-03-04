package com.wishly.wishlistservice.service.impl;

import com.wishly.wishlistservice.config.WishlistProperties;
import com.wishly.wishlistservice.dto.SupportedSiteResponse;
import com.wishly.wishlistservice.service.SupportedSitesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportedSitesServiceImpl implements SupportedSitesService {

    private final WishlistProperties properties;

    @Override
    public List<SupportedSiteResponse> getSupportedSites() {
        return properties.getSupportedSites().stream()
                .filter(WishlistProperties.SupportedSiteConfig::getEnabled)
                .map(site -> new SupportedSiteResponse(site.getName(), site.getDomains(), site.getEnabled()))
                .toList();
    }

    @Override
    public boolean isSupported(String url) {
        String domain = extractDomain(url);
        if (domain == null) {
            return false;
        }

        return properties.getSupportedSites().stream()
                .filter(WishlistProperties.SupportedSiteConfig::getEnabled)
                .anyMatch(site -> site.getDomains().contains(domain));
    }

    @Override
    public String getSiteName(String url) {
        String domain = extractDomain(url);
        if (domain == null) {
            return null;
        }
        return properties.getSupportedSites().stream()
                .filter(WishlistProperties.SupportedSiteConfig::getEnabled)
                .filter(site -> site.getDomains().contains(domain))
                .map(WishlistProperties.SupportedSiteConfig::getName)
                .findFirst()
                .orElse(null);
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();

            if (host == null) {
                return null;
            }
            return host.startsWith("www.") ? host.substring(4) : host;

        } catch (URISyntaxException e) {
            return null;
        }
    }

}
