package com.wishly.wishlistservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "wishlist")
@Data
public class WishlistProperties {
    private List<SupportedSiteConfig> supportedSites = new ArrayList<>();

    @Data
    public static class SupportedSiteConfig {
        private String name;
        private List<String> domains = new ArrayList<>();
        private Boolean enabled = true;
    }
}
