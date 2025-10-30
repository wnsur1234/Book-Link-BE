package com.bookbook.booklink.auth_service.model.dto.response;

import com.bookbook.booklink.auth_service.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "내 프로필 응답 DTO")
public class ProfileResDto {

    @Schema(description = "회원 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "이메일(로그인 ID)", example = "test@example.com")
    private String email;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "북북이")
    private String nickName;

    @Schema(description = "주소", example = "서울시 강남구 역삼동 123-45")
    private String address;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "가입 경로", example = "LOCAL")
    private String provider;

    @Schema(description = "역할", example = "CUSTOMER")
    private String role;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "보유 포인트", example = "1000")
    private Integer pointBalance;

    @Schema(description = "가입 일자", example = "2025-09-23T20:05:00")
    private LocalDateTime createdAt;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;

    @Schema(description = "내가 만든 Library ID (없으면 미포함/NULL)",
            example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID libraryId;

    public static ProfileResDto from(Member m) {
        return ProfileResDto.builder()
                .id(m.getId())
                .email(m.getEmail())
                .name(m.getName())
                .nickName(m.getNickname())
                .address(m.getAddress())
                .phone(m.getPhone())
                .provider(m.getProvider().name())
                .role(m.getRole().name())
                .status(m.getStatus().name())
                // Point가 없을 수도 있으니 NPE 방지
                .pointBalance(m.getPoint() != null ? m.getPoint().getBalance() : 0)
                .createdAt(m.getCreatedAt())
                .profileImage(m.getProfileImage())
                // Library 존재 시에만 ID 세팅
                .libraryId(m.getLibrary() != null ? m.getLibrary().getId() : null)
                .build();
    }
}
