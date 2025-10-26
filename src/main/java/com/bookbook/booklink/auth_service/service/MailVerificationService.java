package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.model.dto.response.VerificationResDto;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import com.bookbook.booklink.auth_service.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailVerificationService {
    private static final String CODE_KEY = "email:verify:code:%s:%s";     // purpose, email
    private static final String TRIES_KEY = "email:verify:tries:%s:%s";   // purpose, email
    private static final String CDN_KEY = "email:verify:cooldown:%s:%s";  // purpose, email

    private final MemberRepository memberRepository;   // 회원가입 용도일 때만 사용
    private final MailSenderService mailSenderService;
    private final RedisService redisService;

    @Value("${app.auth-code-expiration-millis}")
    private long codeTtlMillis;

    private static final int MAX_TRIES = 5;
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    @Transactional
    public void sendCodeToEmail(String email, String purpose) {
        // (선택) 회원가입일 때 이미 존재하면 차단
        if ("REGISTER".equalsIgnoreCase(purpose)) {
            Optional<Member> found = memberRepository.findByEmail(email);
            if (found.isPresent()) throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        // 재발송 쿨다운
        String cdnKey = CDN_KEY.formatted(purpose, email);
        if (redisService.hasKey(cdnKey)) throw new IllegalStateException("재발송 대기 중입니다.");

        String code = generateNumericCode(6);
        String subject = "[Travel With Me] 이메일 인증 코드";
        String body = "인증 코드는 " + code + " 입니다. " + (codeTtlMillis/60000) + "분 내 입력해주세요.";

        // 메일 발송
        mailSenderService.sendPlainText(email, subject, body);

        // 코드 저장
        String codeKey = CODE_KEY.formatted(purpose, email);
        redisService.setValues(codeKey, code, Duration.ofMillis(codeTtlMillis));

        // 쿨다운/시도횟수 초기화
        redisService.setValues(cdnKey, "1", RESEND_COOLDOWN);
        redisService.delete(TRIES_KEY.formatted(purpose, email));
    }

    @Transactional
    public VerificationResDto verifyCode(String email, String purpose, String inputCode) {
        String codeKey = CODE_KEY.formatted(purpose, email);
        String saved = redisService.getValues(codeKey);
        if (saved == null) {
            return VerificationResDto.of(false); // 만료 또는 미발송
        }

        // 시도 제한
        String triesKey = TRIES_KEY.formatted(purpose, email);
        long tries = redisService.increment(triesKey, Duration.ofMillis(codeTtlMillis));
        if (tries > MAX_TRIES) {
            return VerificationResDto.of(false);
        }

        boolean ok = saved.equals(inputCode);
        if (ok) {
            // 성공 시 소비
            redisService.delete(codeKey);
            redisService.delete(triesKey);
        }
        return VerificationResDto.of(ok);
    }

    private String generateNumericCode(int length) {
        try {
            Random r = SecureRandom.getInstanceStrong();
            StringBuilder b = new StringBuilder(length);
            for (int i = 0; i < length; i++) b.append(r.nextInt(10));
            return b.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("코드 생성 실패", e);
        }
    }
}
