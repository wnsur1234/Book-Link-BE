package com.bookbook.booklink.common.service;

import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public String generateIdempotencyKey(String prefix, String traceId) {
        return prefix + ":" + traceId;
    }

    /**
     * Redis를 이용한 멱등성 체크 후 이벤트 발행
     *
     * @param ttl           Lock 유지 시간 (분 단위)
     * @param eventSupplier 이벤트 생성 함수 (중복 요청이 아닌 경우 발행할 이벤트)
     * @throws CustomException 중복 요청일 경우
     */
    public <T> void checkIdempotency(String key, long ttl, Supplier<T> eventSupplier) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "LOCK", ttl, TimeUnit.MINUTES);

        if (Boolean.FALSE.equals(success)) {
            throw new CustomException(ErrorCode.DUPLICATE_REQUEST);
        }

        // DB 실패 시 롤백을 위해 이벤트 발행
        T event = eventSupplier.get();
        eventPublisher.publishEvent(event);
    }

}
