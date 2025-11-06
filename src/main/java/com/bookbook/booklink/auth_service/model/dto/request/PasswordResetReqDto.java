package com.bookbook.booklink.auth_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/])\\S{8,20}$",
            message = "비밀번호는 대문자/소문자/숫자/특수문자를 각각 1자 이상 포함해야 하며 공백을 포함할 수 없습니다."
    )
    private String newPassword;

    @Schema(description = "새로운 비밀번호 확인", example = "NewP@ssw0rd!")
    @NotBlank
    private String confirmPassword;

}