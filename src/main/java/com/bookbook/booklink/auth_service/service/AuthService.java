package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.auth_service.model.dto.request.LoginReqDto;
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

    // RefreshToken을 body에 담지 않게 하기 위한 recode 구조
    public record LoginResult(String accessToken, String refreshToken) { }
}
