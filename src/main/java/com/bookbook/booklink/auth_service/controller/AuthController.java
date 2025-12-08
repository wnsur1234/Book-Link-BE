package com.bookbook.booklink.auth_service.controller;

import com.bookbook.booklink.auth_service.controller.docs.AuthApiDocs;
import com.bookbook.booklink.auth_service.model.dto.request.LoginReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.TokenResDto;
import com.bookbook.booklink.auth_service.service.AuthService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApiDocs {

    private final AuthService authService;

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

        AuthService.LoginResult loginResult = authService.login(loginReqDto);

        // HttpOnly RefreshToken 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken",loginResult.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Strict") // csrf 설정
                .maxAge(60 * 60 * 24 * 7)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(BaseResponse.success(new TokenResDto(loginResult.accessToken())));
    }

    @Override
    public ResponseEntity<BaseResponse<TokenResDto>> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        String newAccessToken = authService.reissue(refreshToken);

        return ResponseEntity.ok()
                .body(BaseResponse.success(new TokenResDto(newAccessToken)));
    }
}