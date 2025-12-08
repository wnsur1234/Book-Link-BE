package com.bookbook.booklink.auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResDto {
    //TODO
    // jwt토큰 발급 시 회원가입 완료 후 바로 토큰 브라우저에 응답하도록 추가
    private UUID id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String profileImage;

    public static SignUpResDto from(SignUpResDto dto) {
        return SignUpResDto.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .profileImage(dto.getProfileImage())
                .build();
    }
}
