package com.bookbook.booklink.auth_service.controller;

import com.bookbook.booklink.auth_service.controller.docs.AuthApiDocs;
import com.bookbook.booklink.auth_service.model.dto.request.LoginReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.TokenResDto;
import com.bookbook.booklink.auth_service.service.AuthService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import com.bookbook.booklink.common.jwt.service.RefreshTokenService;
import com.bookbook.booklink.common.jwt.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public ResponseEntity<BaseResponse<Boolean>> logout(@AuthenticationPrincipal CustomUserDetails user){
        authService.logout(user.getUsername()); // email이 담겨있음

        // 쿠키 삭제용 빈 쿠키 생성
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // HTTPS만 사용 시
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // = 삭제
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(BaseResponse.success(true));
    }

    @Override
    public ResponseEntity<BaseResponse<TokenResDto>> login(@Valid @RequestBody LoginReqDto loginReqDto){

        // 인증 시도
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword()));

        // 인증 성공 시 사용자 정보/권한 획득
        String email = authentication.getName();

        // 권한 → JWT role 클레임(OWNER/CUSTOMER)로 정규화
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .orElse("CUSTOMER");

        //JWT 발급
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // 기존 리프레시 토큰 삭제 후 신규 저장
        refreshTokenService.saveRefreshToken(email, refreshToken);

        // HttpOnly RefreshToken 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict") // csrf 설정
                .maxAge(60 * 60 * 24 * 7)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(BaseResponse.success(new TokenResDto(accessToken)));
    }
}