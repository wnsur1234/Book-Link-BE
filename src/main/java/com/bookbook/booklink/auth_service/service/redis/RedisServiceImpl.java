package com.bookbook.booklink.auth_service.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final StringRedisTemplate template;

    @Override
    public void setValues(String key, String value, Duration ttl) {
        template.opsForValue().set(key, value, ttl);
    }

    @Override
    public String getValues(String key) {
        return template.opsForValue().get(key);
    }

    @Override
    public boolean hasKey(String key) {
        Boolean r = template.hasKey(key);
        return r != null && r;
    }

    @Override
    public void delete(String key) {
        template.delete(key);
    }

    @Override
    public long increment(String key, Duration ttlIfNew) {
        Long val = template.opsForValue().increment(key);
        if (val != null && val == 1L) {
            template.expire(key, ttlIfNew);
        }
        return val == null ? 0L : val;
    }
}
