package com.bookbook.booklink.auth_service.controller;

import com.bookbook.booklink.auth_service.controller.docs.EmailApiDocs;
import com.bookbook.booklink.auth_service.model.dto.request.SendCodeReqDto;
import com.bookbook.booklink.auth_service.model.dto.request.VerifyCodeReqDto;
import com.bookbook.booklink.auth_service.model.dto.response.VerificationResDto;
import com.bookbook.booklink.auth_service.service.MailVerificationService;
import com.bookbook.booklink.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@RequiredArgsConstructor
public class EmailVerificationController implements EmailApiDocs {

    private final MailVerificationService mailVerificationService;

    @Override
    public ResponseEntity<BaseResponse<Boolean>> sendMessage(
            @Valid @RequestBody SendCodeReqDto req){
        mailVerificationService.sendCodeToEmail(req.getEmail(), req.getPurpose());
        return ResponseEntity.ok()
            .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<VerificationResDto>> verify(
            @Valid @RequestBody VerifyCodeReqDto req
    ){
        VerificationResDto result =
                mailVerificationService.verifyCode(req.getEmail(), req.getPurpose(), req.getCode());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
