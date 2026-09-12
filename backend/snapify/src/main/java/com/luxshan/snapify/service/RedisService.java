package com.luxshan.snapify.service;


import com.luxshan.snapify.dto.CachedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void set(String key, CachedLink cachedLink) {
        try {
            String value = objectMapper.writeValueAsString(cachedLink);
            redisTemplate.opsForValue().set(key, value);
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
}