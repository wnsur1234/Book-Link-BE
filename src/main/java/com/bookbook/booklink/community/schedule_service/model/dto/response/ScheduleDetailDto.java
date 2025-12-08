package com.bookbook.booklink.community.schedule_service.model.dto.response;


import com.bookbook.booklink.community.group_service.model.dto.response.ParticipantMemberListDto;
import com.bookbook.booklink.community.schedule_service.model.GroupSchedule;
import com.bookbook.booklink.community.schedule_service.model.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임 일정 상세 정보를 담는 응답 DTO")
public class ScheduleDetailDto {

    @Schema(description = "일정의 고유 식별 ID", example = "f0e9d8c7-b6a5-4e3d-2c1b-0a9876543210")
    private UUID id;

    @Schema(description = "일정 제목", example = "SF 소설 '듄' 온라인 토론")
    private String title;

    @Schema(description = "일정 소개 및 상세 내용", example = "책 내용 요약 및 인상 깊었던 부분 공유")
    private String description;

    @Schema(description = "일정 시작 시간 (날짜 및 시간 포함)", example = "2025-10-30T19:00:00")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간 (선택 사항)", example = "2025-10-30T21:00:00")
    private LocalDateTime endTime;

    @Schema(description = "현재 일정 참여 인원수", example = "5")
    private Integer participantCount;

    @Schema(description = "모이는 장소 또는 온라인 링크", example = "강남 스터디룸 5호실")
    private String location;

    @Schema(description = "일정 상태 (PLANNED, CANCELLED, COMPLETED)", example = "PLANNED")
    private ScheduleStatus status;

    @Schema(description = "일정 생성 시간", example = "2025-10-15T10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "참여자 목록")
    private List<ParticipantMemberListDto> memberList;

    public static ScheduleDetailDto fromEntity(GroupSchedule groupSchedule, List<ParticipantMemberListDto> memberList) {
        return ScheduleDetailDto.builder()
                .id(groupSchedule.getId())
                .title(groupSchedule.getTitle())
                .description(groupSchedule.getDescription())
                .startTime(groupSchedule.getStartTime())
                .endTime(groupSchedule.getEndTime())
                .participantCount(groupSchedule.getParticipantCount())
                // .maxCapacity(groupSchedule.getMaxCapacity()) // 엔티티에 있다면 추가
                .location(groupSchedule.getLocation())
                .status(groupSchedule.getStatus())
                .createTime(groupSchedule.getCreateTime())
                .memberList(memberList)
                .build();
    }
}