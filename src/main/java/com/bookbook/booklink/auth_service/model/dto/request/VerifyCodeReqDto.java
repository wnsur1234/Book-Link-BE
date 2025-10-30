package com.bookbook.booklink.auth_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeReqDto {
    @Schema(description = "인증코드", example = "483920")
    @NotBlank @Size(min=6, max=8)
    private String code;
}
