package com.bookbook.booklink.auth_service.controller;

import com.bookbook.booklink.auth_service.controller.docs.PasswordApiDocs;
import com.bookbook.booklink.auth_service.model.dto.request.EmailReqDto;
import com.bookbook.booklink.auth_service.model.dto.request.PasswordResetReqDto;
import com.bookbook.booklink.auth_service.service.PasswordResetService;
import com.bookbook.booklink.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordResetController implements PasswordApiDocs {

    private final PasswordResetService passwordResetService;

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<BaseResponse<Boolean>> requestLink(
            @Valid @RequestBody EmailReqDto req
    ){
        Boolean result = passwordResetService.issueResetTokenAndSendMail(req.getEmail());
        // 존재 유추 방지: 항상 true 반환
        return ResponseEntity.ok()
                .body(BaseResponse.success(result));
    }

    @Override
    @PreAuthorize("permitAll()")
    public ResponseEntity<BaseResponse<Boolean>> resetPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody PasswordResetReqDto req
    ) {
        passwordResetService.resetPassword(token, req);
        return ResponseEntity.ok(BaseResponse.success(true));
    }

}
