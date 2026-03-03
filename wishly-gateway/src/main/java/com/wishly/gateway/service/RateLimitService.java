package com.wishly.gateway.service;

import reactor.core.publisher.Mono;

public interface RateLimitService {
    Mono<Boolean> tryConsume(String identifier);
}
