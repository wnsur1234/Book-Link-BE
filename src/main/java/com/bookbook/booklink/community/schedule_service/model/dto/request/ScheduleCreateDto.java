package com.bookbook.booklink.community.schedule_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Schema(description = "새 모임 일정을 생성하기 위한 요청 DTO")
public class ScheduleCreateDto {

    @NotNull(message = "그룹 ID는 필수입니다.")
    @Schema(description = "일정이 속한 **그룹 ID** (필수)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID groupId;

    @NotBlank(message = "일정 제목은 필수 입력 항목입니다.")
    @Size(min = 1, max = 100, message = "일정 제목은 1자에서 100자 사이여야 합니다.")
    @Schema(description = "**일정 제목** (필수, 1~100자)", example = "SF 소설 '듄' 온라인 토론", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "일정 소개 및 상세 내용 (선택)", example = "책 내용 요약 및 인상 깊었던 부분 공유")
    private String description;

    @NotNull(message = "일정 시작 시간은 필수입니다.")
    @Schema(description = "**일정 시작 시간** (필수: 날짜 및 시간 포함)", example = "2025-10-30T19:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간 (선택)", example = "2025-10-30T21:00:00")
    private LocalDateTime endTime;

    @Schema(description = "모이는 장소 또는 온라인 링크 (선택)", example = "Zoom 링크: https://zoom.us/j/1234567890")
    private String location;
}