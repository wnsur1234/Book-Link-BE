package com.bookbook.booklink.auth_service.controller.docs;

import com.bookbook.booklink.auth_service.model.dto.request.EmailReqDto;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth/password-reset")
public interface PasswordApiDocs {

    @Operation(
            summary = "비밀번호 재설정 링크 발송",
            description = "회원 이메일로 비밀번호 재설정 링크(토큰 포함)를 발송합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PostMapping("/request")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BaseResponse<Boolean>> requestLink(
            @Valid @RequestBody EmailReqDto req
    );
}
