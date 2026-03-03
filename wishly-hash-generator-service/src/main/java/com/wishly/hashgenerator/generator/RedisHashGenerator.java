package com.wishly.hashgenerator.generator;

import com.wishly.common.generator.HashGenerator;
import com.wishly.common.exception.HashPoolExhaustedException;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisHashGenerator implements HashGenerator {

    private final StringRedisTemplate redisTemplate;

    @Value("${hash.generator.pool-key:hash:pool}")
    private String poolKey;

    @Value("${hash.generator.pool.threshold:100}")
    private int poolThreshold;

    public RedisHashGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generate(int length) {
        try {
            Long size = redisTemplate.opsForList().size(poolKey);
            if (size != null && size < poolThreshold / 2) {
                log.warn("Hash pool critically low: {}", size);
            }

            String hash = redisTemplate.opsForList().leftPop(poolKey);
            if (hash == null) {
                throw new HashPoolExhaustedException();
            }
            return hash;
        } catch (RedisConnectionException e) {
            log.error("Redis connection failed: {}", e.getMessage(), e);
            throw new RuntimeException("Service temporarily unavailable", e);
        }
    }
}
