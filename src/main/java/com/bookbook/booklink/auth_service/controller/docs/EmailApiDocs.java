package com.bookbook.booklink.auth_service.controller.docs;

import com.bookbook.booklink.auth_service.model.dto.request.SendCodeReqDto;
import com.bookbook.booklink.auth_service.model.dto.request.VerifyCodeReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.VerificationResDto;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth/email")
@Tag(name = "Mail API", description = "이메일 인증 관련 API")
public interface EmailApiDocs {

    @Operation(
            summary = "이메일인증 요청",
            description = "인증 하고자하는 사용자의 이메일을 통해 인증요청을 보냅니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PostMapping("/code")
    public ResponseEntity<BaseResponse<Boolean>> sendMessage(
            @Valid @RequestBody SendCodeReqDto req);

    @Operation(
            summary = "이메일 인증 검증 및 처리",
            description = "이메일을 통해 받은 인증 코드를 적어 인증 성공여부를 반환한다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping("/verify")
    public ResponseEntity<BaseResponse<VerificationResDto>> verify(
            @Valid @RequestBody VerifyCodeReqDto req,
            @RequestHeader("Email") String email
    );

}
