package com.pastebin.gateway.filter;

import com.pastebin.gateway.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {
    private final RateLimitService rateLimitService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String identifier = getIdentifier(exchange);

        return rateLimitService.tryConsume(identifier)
                .flatMap(allowed -> {
                    if (allowed) {
                        return chain.filter(exchange);
                    } else {
                        return rateLimitResponse(exchange);
                    }
                });
    }

    private Mono<Void> rateLimitResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "status", 429,
                "error", "Too Many Requests",
                "message", "Rate limit exceeded. Please try again later.",
                "retryAfter", 1
        );

        byte[] bytes = """
                {"status":429,"error":"Too Many Requests","message":"Rate limit exceeded. Please try again later."}
                """.getBytes();

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(bytes)));
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/actuator") ||
                path.startsWith("/health") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/login");
    }

    private String getIdentifier(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (userId != null) {
            return "user:" + userId;
        }

        String ip = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
        return "ip:" + ip;
    }

    @Override
    public int getOrder() {
        return -200;
    }

}
