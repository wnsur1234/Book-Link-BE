package com.bookbook.booklink.common.oauth;

import com.bookbook.booklink.common.jwt.service.RefreshTokenService;
import com.bookbook.booklink.common.jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        CustomOAuth2User oauthUser =
                (CustomOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getMember().getEmail();
        String role = oauthUser.getMember().getRole().name();

        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email);

        refreshTokenService.saveRefreshToken(email, refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(60 * 60 * 24 * 7)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(
                "http://localhost:3000/oauth/callback?accessToken=" + accessToken
        );
    }
}
