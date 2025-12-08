package com.bookbook.booklink.community.schedule_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.community.group_service.model.Group;
import com.bookbook.booklink.community.schedule_service.model.dto.request.ScheduleCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임(그룹) 일정 정보 엔티티")
public class GroupSchedule {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "일정의 고유 식별 ID", example = "f0e9d8c7-b6a5-4e3d-2c1b-0a9876543210")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @NotNull(message = "일정은 반드시 그룹에 속해야 합니다.")
    @Schema(description = "일정이 속한 그룹 (필수)")
    private Group group;

    @Column(nullable = false, length = 100)
    @Size(min = 1, max = 100, message = "일정 제목은 1자에서 100자 사이여야 합니다.")
    @Schema(description = "일정 제목 (필수, 1~100자)", example = "SF 소설 '듄' 토론회")
    private String title;

    @Column(length = 1000)
    @Schema(description = "일정 소개 및 상세 내용 (선택)", example = "책 내용 요약 및 인상 깊었던 부분 공유")
    private String description;

    @Column(nullable = false)
    @Schema(description = "일정 시작 시간 (필수: 날짜 및 시간 포함)", example = "2025-10-30T19:00:00")
    private LocalDateTime startTime;

    @Schema(description = "일정 종료 시간 (선택)", example = "2025-10-30T21:00:00")
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Min(value = 1)
    @Builder.Default
    @Schema(description = "현재 일정 참여 인원수 (최소 1명부터 시작)", example = "5")
    private Integer participantCount = 1;

    @Column(length = 255)
    @Schema(description = "모이는 장소 또는 온라인 링크", example = "강남 스터디룸 5호실")
    private String location;

    @Column(nullable = false, updatable = false)
    @Schema(description = "일정 주최자(Host)의 사용자 ID (변경 불가)", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID hostId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "일정 상태 (PLANNED, CANCELLED, COMPLETED 등)", example = "PLANNED")
    private ScheduleStatus status = ScheduleStatus.PLANNED;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "일정 생성 시간 (자동 생성, 변경 불가)")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "일정에 참여하는 멤버 목록 (ScheduleParticipant 엔티티와 연결)")
    private List<ScheduleParticipant> participantList = new ArrayList<>();

    // --------------------------------------------------------------------------------
    // 비즈니스 로직
    // --------------------------------------------------------------------------------

    public static GroupSchedule toEntity(ScheduleCreateDto scheduleCreateDto, Group group, Member member) {
        return GroupSchedule.builder()
                .group(group)
                .title(scheduleCreateDto.getTitle())
                .description(scheduleCreateDto.getDescription())
                .startTime(scheduleCreateDto.getStartTime())
                .endTime(scheduleCreateDto.getEndTime())
                .location(scheduleCreateDto.getLocation())
                .hostId(member.getId())
                .build();
    }

    public void update(ScheduleCreateDto scheduleCreateDto) {
        this.title = scheduleCreateDto.getTitle();
        this.description = scheduleCreateDto.getDescription();
        this.startTime = scheduleCreateDto.getStartTime();
        this.endTime = scheduleCreateDto.getEndTime();
        this.location = scheduleCreateDto.getLocation();
    }

    public void add() {
        this.participantCount++;
    }

    public void remove() {
        if (this.participantCount > 0) {
            this.participantCount--;
        }
    }
}