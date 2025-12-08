package com.bookbook.booklink.auth_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "MemberSignUpDto", description = "회원가입 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpReqDto {
    @Email
    @NotBlank
    @Schema(description = "이메일 (로그인 ID)", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String email;

    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/])\\S{8,20}$",
            message = "비밀번호는 대문자/소문자/숫자/특수문자를 각각 1자 이상 포함해야 하며 공백을 포함할 수 없습니다."
    )
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;

    @Size(min = 5, max = 200)
    private String address;

    @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$", message = "전화번호 형식은 01x-xxxx-xxxx 입니다.")
    private String phone;

    @Size(max = 500)
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", maxLength = 500)
    private String profileImage;

}
