package com.bookbook.booklink.auth_service.controller;

import com.bookbook.booklink.auth_service.model.dto.request.PasswordReqDto;
import com.bookbook.booklink.auth_service.service.PasswordCheckService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import com.bookbook.booklink.auth_service.controller.docs.MemberApiDocs;
import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.model.dto.request.SignUpReqDto;
import com.bookbook.booklink.auth_service.model.dto.request.UpdateReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.ProfileResDto;
import com.bookbook.booklink.auth_service.service.MemberService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MemberController implements MemberApiDocs {

    private final MemberService memberService;
    private final PasswordCheckService passwordCheckService;

    @Override
    public ResponseEntity<BaseResponse<Boolean>> signup(
            @Valid @RequestBody SignUpReqDto request,
            @RequestHeader(value = "Trace-Id", required = false) String traceId
    ) {
        Member saved = memberService.signUp(request, traceId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<ProfileResDto>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UUID memberID = user.getMember().getId();
        ProfileResDto response = memberService.getMyProfile(memberID);

        return ResponseEntity.ok()
                .body(BaseResponse.success(response));
    }

    @Override
    public ResponseEntity<BaseResponse<ProfileResDto>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateReqDto reqDto
    ) {
        UUID memberId = user.getMember().getId();
        Member updated = memberService.updateProfile(memberId, reqDto);
        return ResponseEntity.ok(BaseResponse.success(ProfileResDto.from(updated)));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> checkPassword(
            @Valid @RequestBody PasswordReqDto passwordReqDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Boolean matches = passwordCheckService.verifyPassword(user.getMember().getEmail(),passwordReqDto.getPassword());
        return ResponseEntity.ok()
                .body(BaseResponse.success(matches));
    }
}
    