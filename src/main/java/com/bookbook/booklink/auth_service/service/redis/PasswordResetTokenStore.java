package com.bookbook.booklink.auth_service.service.redis;

import java.time.Duration;
import java.util.Optional;

public interface PasswordResetTokenStore {
    String create(String email, Duration ttl);            // 토큰 생성 & 저장
    Optional<String> getEmailByToken(String token);       // 검증용 조회
    void consume(String token);                           // 1회 사용 후 즉시 폐기
}
