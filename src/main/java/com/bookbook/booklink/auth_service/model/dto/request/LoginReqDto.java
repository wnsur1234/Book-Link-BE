package com.bookbook.booklink.auth_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {

    @Schema(description = "사용자 이메일", example = "test1@example.com")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "Piltopgkm@1818")
    private String password;
}