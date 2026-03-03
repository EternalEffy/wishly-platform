package com.wishly.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitConfig {
    private int maxTokens = 100;

    private int refillRate = 10;

    private String keyPrefix = "rate-limit";

    private boolean enabled = true;
}
