package com.pastebin.gateway.service.impl;

import com.pastebin.gateway.config.RateLimitConfig;
import com.pastebin.gateway.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RateLimitConfig config;

    private static final String TOKENS_KEY = ":tokens";
    private static final String TIMESTAMP_KEY = ":timestamp";

    @Override
    public Mono<Boolean> tryConsume(String identifier) {
        if (!config.isEnabled()) {
            return Mono.just(true);
        }

        String baseKey = config.getKeyPrefix() + identifier;
        String tokensKey = baseKey + TOKENS_KEY;
        String timestampKey = baseKey + TIMESTAMP_KEY;
        long now = Instant.now().getEpochSecond();

        return Mono.zip(
                        redisTemplate.opsForValue().get(tokensKey),
                        redisTemplate.opsForValue().get(timestampKey)
                )
                .flatMap(tuple -> {
                    String tokensStr = tuple.getT1();
                    String timestampStr = tuple.getT2();

                    long currentTokens = Long.parseLong(tokensStr);
                    long lastTimestamp = Long.parseLong(timestampStr);

                    long elapsed = now - lastTimestamp;
                    long newTokens = Math.min(config.getMaxTokens(),
                            currentTokens + (elapsed * config.getRefillRate()));

                    if (newTokens >= 1) {
                        newTokens--;
                        return saveState(tokensKey, timestampKey, newTokens, now)
                                .thenReturn(true);
                    } else {
                        log.warn("Rate limit exceeded for: {}", identifier);
                        return Mono.just(false);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> saveState(tokensKey, timestampKey, config.getMaxTokens() - 1, now)
                        .thenReturn(true)))
                .onErrorResume(e -> {
                    log.error("Redis error in rate limiting: {}", e.getMessage(), e);
                    return Mono.just(true);
                });
    }

    private Mono<Void> saveState(String tokensKey, String timestampKey,
                                 long tokens, long timestamp) {
        return redisTemplate.opsForValue()
                .multiSet(Map.of(
                        tokensKey, String.valueOf(tokens),
                        timestampKey, String.valueOf(timestamp)
                ))
                .then();
    }
}