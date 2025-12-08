package com.bookbook.booklink.community.schedule_service.model.dto.response;

import com.bookbook.booklink.community.schedule_service.model.GroupSchedule;
import com.bookbook.booklink.community.schedule_service.model.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임 일정 목록 조회를 위한 간략 응답 DTO")
public class ScheduleListDto {

    @Schema(description = "일정의 고유 식별 ID", example = "f0e9d8c7-b6a5-4e3d-2c1b-0a9876543210")
    private UUID id;

    @Schema(description = "일정이 속한 그룹의 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID groupId;

    @Schema(description = "일정이 속한 그룹의 이름", example = "월간 독서 클럽")
    private String groupName;

    @Schema(description = "일정 제목", example = "10월의 책 '이방인' 토론")
    private String title;

    @Schema(description = "일정 소개", example = "카뮈의 이방인에 대한 심도 있는 논의")
    private String description;

    @Schema(description = "일정 시작 시간 (날짜 및 시간 포함)", example = "2025-10-30T19:00:00")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간 (선택 사항)", example = "2025-10-30T21:00:00")
    private LocalDateTime endTime;

    @Schema(description = "현재 일정 참여 인원수", example = "8")
    private Integer participantCount;

    @Schema(description = "모이는 장소 또는 온라인 링크", example = "온라인 회의실")
    private String location;

    @Schema(description = "일정 상태 (PLANNED, CANCELLED, COMPLETED)", example = "PLANNED")
    private ScheduleStatus status;

    @Schema(description = "일정 생성 시간", example = "2025-10-01T10:00:00")
    private LocalDateTime createTime;

    public static ScheduleListDto fromEntity(GroupSchedule groupSchedule) {
        return ScheduleListDto.builder()
                .id(groupSchedule.getId())
                .groupId(groupSchedule.getGroup().getId())
                .groupName(groupSchedule.getGroup().getName())
                .title(groupSchedule.getTitle())
                .description(groupSchedule.getDescription())
                .startTime(groupSchedule.getStartTime())
                .endTime(groupSchedule.getEndTime())
                .participantCount(groupSchedule.getParticipantCount())
                .location(groupSchedule.getLocation())
                .status(groupSchedule.getStatus())
                .createTime(groupSchedule.getCreateTime())
                .build();
    }
}