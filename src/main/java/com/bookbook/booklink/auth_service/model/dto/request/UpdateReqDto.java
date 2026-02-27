package com.bookbook.booklink.auth_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원 정보 수정 요청 DTO")
public class UpdateReqDto {

    @Size(min = 2, max = 20)
    @Schema(description = "닉네임", example = "북북이")
    private String nickname;

    @Size(min = 5, max = 200)
    @Schema(description = "주소", example = "서울시 강남구 역삼동 123-45")
    private String address;

    @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$",
             message = "전화번호 형식은 01x-xxxx-xxxx 입니다.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Size(max = 500)
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;
}
