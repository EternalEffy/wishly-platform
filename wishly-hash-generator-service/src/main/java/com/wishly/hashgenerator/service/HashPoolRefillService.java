package com.wishly.hashgenerator.service;

import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
public class HashPoolRefillService {

    @Value("${hash.generator.pool-key:hash:pool}")
    private String poolKey;

    @Value("${hash.generator.pool.threshold:100}")
    private int poolThreshold;

    @Value("${hash.generator.pool.batch-size:1000}")
    private int poolBatchSize;

    @Value("${hash.generator.length:8}")
    private int hashLength;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(fixedDelay = 5000)
    public void refillPool() {
        try {
            Long size = redisTemplate.opsForList().size(poolKey);
            if (size != null && size < poolThreshold) {
                log.info("Refilling hash pool: current size={}, adding {}", size, poolBatchSize);
                for (int i = 0; i < poolBatchSize; i++) {
                    redisTemplate.opsForList().rightPush(poolKey, generateHash(hashLength));
                }
            }
        } catch (RedisConnectionException e) {
            log.error("Redis connection failed: {}", e.getMessage());
        }
    }

    private String generateHash(int length) {
        StringBuilder hash = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            hash.append(CHARACTERS.charAt(index));
        }
        return hash.toString();
    }
}
