package com.bookbook.booklink.common.jwt;

import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import com.bookbook.booklink.common.jwt.service.RefreshTokenService;
import com.bookbook.booklink.common.jwt.util.JWTUtil;
import com.bookbook.booklink.auth_service.model.dto.request.LoginReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // 로그인 시도 (아이디/비밀번호 확인)
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoginReqDto loginReqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword());

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.USER_LOGIN_FAILED);
        }
    }

    // 로그인 성공 → AccessToken, RefreshToken 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();

        String accessToken = jwtUtil.createAccessToken(principal.getUsername(), principal.getRole());
        String refreshToken = jwtUtil.createRefreshToken(principal.getUsername());

        // 기존 RefreshToken 삭제 후 새로 저장
        refreshTokenService.saveRefreshToken(principal.getUsername(), refreshToken);

        // 응답 헤더에 담아 보내기
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", refreshToken);
    }
}
