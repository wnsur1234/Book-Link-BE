package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.auth_service.model.dto.response.VerificationResDto;
import com.bookbook.booklink.auth_service.service.redis.RedisService;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailVerificationService {
    private static final String CODE_KEY = "email:verify:code:%s";     // purpose, email
    private static final String TRIES_KEY = "email:verify:tries:%s";   // purpose, email
    private static final String CDN_KEY = "email:verify:cooldown:%s";  // purpose, email

    private final MailSenderService mailSenderService;
    private final RedisService redisService;

    @Value("${app.auth-code-expiration-millis}")
    private long codeTtlMillis;

    private static final int MAX_TRIES = 5;
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    @Transactional
    public void sendCodeToEmail(String email) {

        // 재발송 쿨다운
        String cdnKey = CDN_KEY.formatted(email);
        if (redisService.hasKey(cdnKey)) {
            throw new CustomException(ErrorCode.EMAIL_COOLDOWN);
        }

        String code = generateNumericCode(6);
        String subject = "[BookLink] 이메일 인증 코드";
        String body = "인증 코드는 " + code + " 입니다. " + (codeTtlMillis/60000) + "분 내 입력해주세요.";

        // 메일 발송
        mailSenderService.sendPlainText(email, subject, body);

        // 코드 저장
        String codeKey = CODE_KEY.formatted(email);
        redisService.setValues(codeKey, code, Duration.ofMillis(codeTtlMillis));

        // 쿨다운/시도횟수 초기화
        redisService.setValues(cdnKey, "1", RESEND_COOLDOWN);
        redisService.delete(TRIES_KEY.formatted(email));
    }

    @Transactional
    public VerificationResDto verifyCode(String email, String inputCode) {
        String codeKey = CODE_KEY.formatted(email);
        String saved = redisService.getValues(codeKey);

        if (saved == null) {
            return VerificationResDto.of(false); // 만료 또는 미발송
        }

        // 시도 제한
        String triesKey = TRIES_KEY.formatted( email);
        long tries = redisService.increment(triesKey, Duration.ofMillis(codeTtlMillis));
        if (tries > MAX_TRIES) {
            throw new CustomException(ErrorCode.AUTH_CODE_TRY_LIMIT_EXCEEDED);
        }

        boolean ok = saved.equals(inputCode);
        if (ok) {
            // 성공 시 소비
            redisService.delete(codeKey);
            redisService.delete(triesKey);
            return VerificationResDto.of(true);
        }else {
            throw new CustomException(ErrorCode.AUTH_CODE_INVALID);
        }
    }

    private String generateNumericCode(int length) {
        try {
            Random r = SecureRandom.getInstanceStrong();
            StringBuilder b = new StringBuilder(length);
            for (int i = 0; i < length; i++) b.append(r.nextInt(10));
            return b.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
