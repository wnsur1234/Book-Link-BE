package com.bookbook.booklink.auth_service.service.redis;

import java.time.Duration;

public interface RedisService {
    void setValues(String key, String value, Duration ttl);
    String getValues(String key);
    boolean hasKey(String key);
    void delete(String key);
    long increment(String key, Duration ttlIfNew);
}

