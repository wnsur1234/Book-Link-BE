package com.bookbook.booklink.auth_service.controller;

import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.model.RefreshToken;
import com.bookbook.booklink.common.jwt.repository.RefreshTokenRepository;
import com.bookbook.booklink.common.jwt.util.JWTUtil;
import com.bookbook.booklink.auth_service.controller.docs.TokenApiDocs;
import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements TokenApiDocs {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public ResponseEntity<BaseResponse<String>> reissue(
            @RequestHeader("Refresh-Token") String refreshToken,
            @RequestHeader(value = "Trace-Id", required = false) String traceId
    ) {
        // refreshToken의 만료 시간을 검사
        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String email = jwtUtil.getUsername(refreshToken);
        RefreshToken saved = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!saved.getToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        String role = member.getRole().name();
        String newAccessToken = jwtUtil.createAccessToken(email, role);
        return ResponseEntity.ok()
                .body(BaseResponse.success("Bearer " + newAccessToken));
    }
}

