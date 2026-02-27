package com.bookbook.booklink.auth_service.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisPasswordResetTokenStore implements PasswordResetTokenStore {

    private final StringRedisTemplate redis;
    private static final String KEY_PREFIX = "pwd:reset:";

    @Override
    public String create(String email, Duration ttl) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = KEY_PREFIX + token;
        redis.opsForValue().set(key, email, ttl);
        return token;
    }

    @Override
    public Optional<String> getEmailByToken(String token) {
        String email = redis.opsForValue().get(KEY_PREFIX + token);
        return Optional.ofNullable(email);
    }

    @Override
    public void consume(String token) {
        redis.delete(KEY_PREFIX + token);
    }
}