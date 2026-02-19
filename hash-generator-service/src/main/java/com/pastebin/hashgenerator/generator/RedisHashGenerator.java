package com.pastebin.hashgenerator.generator;

import com.pastebin.common.generator.HashGenerator;
import com.pastebin.common.exception.HashPoolExhaustedException;
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
            if (size != null && size < poolThreshold / 2) {  // ← Критический уровень
                log.warn("Hash pool critically low: {}", size);
            }

            String hash = redisTemplate.opsForList().leftPop(poolKey);
            if (hash == null) {
                throw new HashPoolExhaustedException("No hashes available in pool");
            }
            return hash;
        } catch (RedisConnectionException e) {
            log.error("Redis connection failed: {}", e.getMessage());
            throw new HashPoolExhaustedException("Redis unavailable");
        }
    }
}
