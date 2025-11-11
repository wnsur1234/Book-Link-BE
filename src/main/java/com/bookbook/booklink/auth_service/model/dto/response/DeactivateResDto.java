package com.bookbook.booklink.auth_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회원 탈퇴(비활성화) 응답 DTO")
public class DeactivateResDto {

    @Schema(description = "비활성화 처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "재활성화 가능 마감일", example = "2025-11-06T23:59:59")
    private LocalDateTime reactivatableUntil;
}
