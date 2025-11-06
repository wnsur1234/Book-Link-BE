package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.model.dto.request.PasswordResetReqDto;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import com.bookbook.booklink.auth_service.service.redis.PasswordResetTokenStore;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.frontend.password-reset.path}")
    private String resetPath;

    @Value("${app.frontend.password-reset.expire-minutes}")
    private long expireMinutes;

    private final MemberRepository memberRepository;
    private final PasswordResetTokenStore tokenStore; // Redis/DB 저장소
    private final MailSenderService mailSenderService; // 팀의 메일 발송 컴포넌트
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean issueResetTokenAndSendMail(String email) {
        // 1) 존재 여부 조회(로그만 참고). 응답은 항상 true
        boolean exists = memberRepository.existsByEmail(email);

        if (exists) {
            // 2) 토큰 생성 & 저장 (TTL=expireMinutes)
            String token = tokenStore.create(email, Duration.ofMinutes(expireMinutes));

            // 3) 링크 조합
            String link = buildResetLink(token);

            // 4) 메일 전송
            sendResetMail(email, link);
        }

        // 존재 유추 방지: 항상 true 반환
        return true;
    }

    private String buildResetLink(String token) {
        // base-url에 슬래시 유무와 path 결합 안전 처리(간단 버전)
        String base = frontendBaseUrl.endsWith("/") ? frontendBaseUrl.substring(0, frontendBaseUrl.length()-1) : frontendBaseUrl;
        String path = resetPath.startsWith("/") ? resetPath : "/" + resetPath;
        return base + path + "?token=" + token;
    }

    private void sendResetMail(String email, String link) {
        String subject = "[BookLink] 비밀번호 재설정 링크";
        String content = """
                안녕하세요, BookLink입니다.

                아래 링크를 클릭하여 비밀번호를 재설정해 주세요.
                (유효시간: %d분)

                %s

                *본인이 요청하지 않은 경우 이 메일을 무시하셔도 됩니다.
                """.formatted(expireMinutes, link);

        mailSenderService.sendPlainText(email, subject, content);
    }

    @Transactional
    public void resetPassword(String token, PasswordResetReqDto req) {

        // 비밀번호 & 확인 값 일치 여부
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 토큰으로 이메일 조회 (없거나 만료 시 예외)
        String email = tokenStore.getEmailByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND));

        // 4) 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 5) 기존 비밀번호와 동일한지 체크 (선택)
        if (passwordEncoder.matches(req.getNewPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        // 6) 비밀번호 변경
        String encoded = passwordEncoder.encode(req.getNewPassword());
        member.changePassword(encoded); // 엔티티에 메서드 하나 만들어 두면 깔끔

        // 7) 토큰 사용 처리 (재사용 방지)
        tokenStore.consume(token); // Redis key 삭제 등
    }

}
