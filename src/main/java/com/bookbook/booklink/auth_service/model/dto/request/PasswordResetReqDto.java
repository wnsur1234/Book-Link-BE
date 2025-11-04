package com.bookbook.booklink.auth_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 재설정 요청 DTO")
public class PasswordResetReqDto {

    @Schema(description = "새로운 비밀번호", example = "NewP@ssw0rd!")
    @NotBlank
    private String newPassword;

    @Schema(description = "새로운 비밀번호 확인", example = "NewP@ssw0rd!")
    @NotBlank
    private String confirmPassword;

}