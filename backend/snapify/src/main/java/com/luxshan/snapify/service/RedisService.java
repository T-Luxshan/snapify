package com.luxshan.snapify.service;


import com.luxshan.snapify.dto.CachedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void set(String key, CachedLink cachedLink) {
        try {
            String value = objectMapper.writeValueAsString(cachedLink);

            Duration ttl = calculateTtl(cachedLink.getExpiresAt());

            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (JacksonException exception) {
            throw new RuntimeException("Failed to serialize cached link", exception);
        }
    }

    public CachedLink get(String key) {
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value, CachedLink.class);
        } catch (JacksonException  exception) {
            throw new RuntimeException("Failed to deserialize cached link", exception);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public String buildLinkKey(String shortCode) {
        return "link:" + shortCode;
    }

    private Duration calculateTtl(LocalDateTime expiresAt) {

        if (expiresAt == null) {
            return Duration.ofHours(1);
        }
        Duration ttl = Duration.between(
                LocalDateTime.now(),
                expiresAt
        );
        return ttl.isPositive()
                ? ttl
                : Duration.ofSeconds(1);
    }
}