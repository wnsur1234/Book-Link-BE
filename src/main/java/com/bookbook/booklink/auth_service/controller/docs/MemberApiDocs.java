package com.bookbook.booklink.auth_service.controller.docs;

import com.bookbook.booklink.auth_service.model.dto.request.PasswordReqDto;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import com.bookbook.booklink.auth_service.model.dto.request.SignUpReqDto;
import com.bookbook.booklink.auth_service.model.dto.request.UpdateReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.ProfileResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/member")
@Tag(name = "Member API", description = "회원가입/회원정보 관련 API")
public interface MemberApiDocs {

    @Operation(
            summary = "회원 가입",
            description = "사용자로 부터 회원 정보를 입력 받아 가입합니다. "
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.PASSWORD_POLICY_INVALID})
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Boolean>> signup(
            @Valid @RequestBody SignUpReqDto signUpReqDto,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "내 프로필 조회",
            description = "현재 인증된 사용자의 회원 정보를 반환합니다.",
            security = {@SecurityRequirement(name = "bearer-key")}
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.USER_NOT_FOUND})
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<ProfileResDto>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.USER_NOT_FOUND})
    @PutMapping("/update")
    public ResponseEntity<BaseResponse<ProfileResDto>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateReqDto reqDto
    );

    @Operation(summary = "비밀번호 일치 여부 확인", description = "사용자가 입력한 비밀번호가 저장된 비밀번호와 일치하는지 확인합니다.")
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.USER_NOT_FOUND})
    @PostMapping("/check-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Boolean>> checkPassword(
            @Valid @RequestBody PasswordReqDto passwordReqDto,
            @AuthenticationPrincipal CustomUserDetails user
    );
}
