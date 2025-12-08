package com.bookbook.booklink.community.schedule_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임의 일정에 참여한 사용자")
public class ScheduleParticipant {


    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "고유 식별 ID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull
    @Schema(description = "일정에 참여하는 사용자")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    @NotNull
    @Schema(description = "사용자가 참여한 일정")
    private GroupSchedule schedule;

    public static ScheduleParticipant create(Member member, GroupSchedule schedule) {
        return ScheduleParticipant.builder().member(member).schedule(schedule).build();
    }
}
