package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.model.dto.request.LoginReqDto;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.model.RefreshToken;
import com.bookbook.booklink.common.jwt.service.RefreshTokenService;
import com.bookbook.booklink.common.jwt.util.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    public void logout(String email) {
        refreshTokenService.logout(email);
    }

    public LoginResult login(LoginReqDto loginReqDto) {

        // 인증 시도
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginReqDto.getEmail(),
                                loginReqDto.getPassword()
                        )
                );

        // 인증 성공 시 이메일/권한 획득
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. 비활성 계정 복구/차단 로직
        if (member.canReactivate()) {
            // 아직 복구 가능 기간 이내의 비활성 계정 → 자동 ACTIVE로 복구
            member.reactivate();
        } else if (member.isDeactivated()) {
            // 복구 기간도 지났는데 여전히 비활성 → 로그인 차단
            throw new CustomException(ErrorCode.MEMBER_DEACTIVATED);
        }

        // 권한 → JWT role 클레임(OWNER/CUSTOMER)로 정규화
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .orElse("CUSTOMER");

        // JWT 발급
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // 4. 기존 리프레시 토큰 삭제 후 신규 저장
        refreshTokenService.saveRefreshToken(email, refreshToken);

        // 5. 컨트롤러에서 쓸 수 있도록 두 토큰을 같이 반환
        return new LoginResult(accessToken, refreshToken);
    }
    
    /**
     * 🟩 RefreshToken 기반 AccessToken 재발급 (HttpOnly 쿠키 기반)
     */
    public String reissue(String refreshToken) {

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // RT 만료 여부 체크
        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String email = jwtUtil.getUsername(refreshToken);

        // DB에 저장된 RefreshToken 조회
        RefreshToken saved = refreshTokenService.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 저장된 RT와 요청받은 RT 비교
        if (!saved.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Member 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String role = member.getRole().name();

        // 새 AccessToken 생성
        return jwtUtil.createAccessToken(email, role);
    }

    // RefreshToken을 body에 담지 않게 하기 위한 recode 구조
    public record LoginResult(String accessToken, String refreshToken) { }
}
