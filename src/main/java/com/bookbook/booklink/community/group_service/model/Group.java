package com.bookbook.booklink.community.group_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.community.group_service.model.dto.request.GroupCreateDto;
import com.bookbook.booklink.community.schedule_service.model.GroupSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "groups")
@Schema(description = "모임(그룹) 정보 엔티티")
public class Group {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "모임 고유 식별 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;

    @Schema(description = "그룹 썸네일 이미지 경로 (URL)", example = "http://image.url/group_thumb.jpg")
    private String thumbnail;

    @Column(nullable = false, length = 100)
    @Schema(description = "모임 이름 (최대 100자)", example = "책으로 연결되는 사람들", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(nullable = false, length = 1000)
    @Schema(description = "모임에 대한 상세 설명 (최대 1000자)", example = "매주 최신 베스트셀러를 읽고 심도 있는 토론을 진행합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Column(nullable = false)
    @Min(value = 1)
    @Builder.Default
    @Schema(description = "현재 모임 참여 인원수 (최소 1명부터 시작)", example = "1")
    private Integer participantCount = 1;

    @Column(nullable = false)
    @Min(value = 1)
    @Max(value = 30)
    @Schema(description = "모임 최대 참여 인원수 (1 ~ 30명)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maxCapacity;

    @Column(nullable = false)
    @Schema(description = "모임을 만든 주최자 사용자 ID (변경 불가)", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID hostId;

    @Column(nullable = false)
    @Schema(description = "모임을 만든 주최자 사용자 이름 (변경 불가)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String hostName;

    @Column(nullable = false)
    @Schema(description = "비공개 여부 (true: 비밀방, false: 공개방)", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean isPrivate;

    @Column(length = 255)
    @Schema(description = "참가 비밀번호 (비공개방일 경우 해시값 저장)", example = "암호화된 문자열")
    private String password;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(description = "모임 생성 시간 (자동 생성, 변경 불가)")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "모임에 참여한 사용자 목록 (GroupMember 엔티티와 연결)")
    private List<GroupMember> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "모임에 등록된 일정 목록 (GroupSchedule 엔티티와 연결)")
    private List<GroupSchedule> scheduleList = new ArrayList<>();

    /**
     * GroupCreateDto와 생성자 정보를 바탕으로 Group 엔티티를 생성합니다.
     */
    public static Group toEntity(GroupCreateDto groupCreateDto, String password, Member member) {
        return Group.builder()
                .name(groupCreateDto.getName())
                .description(groupCreateDto.getDescription())
                .thumbnail(groupCreateDto.getThumbnail())
                .maxCapacity(groupCreateDto.getMaxCapacity())
                .hostId(member.getId())
                .hostName(member.getName())
                .isPrivate(password != null && !password.isEmpty()) // 비밀번호가 있으면 비공개
                .password(password)
                .build();
    }

    /**
     * 모임 정보를 업데이트합니다.
     */
    public void update(GroupCreateDto groupCreateDto, String password) {
        this.name = groupCreateDto.getName();
        this.description = groupCreateDto.getDescription();
        this.thumbnail = groupCreateDto.getThumbnail();
        this.maxCapacity = groupCreateDto.getMaxCapacity();
        this.isPrivate = password != null && !password.isEmpty();
        this.password = password;
    }

    /**
     * 참여자 수를 1 감소시킵니다.
     */
    public void removeParticipant() {
        if (this.participantCount > 0) {
            this.participantCount--;
        }
    }

    /**
     * 참여자 수를 1 증가시킵니다.
     */
    public void addParticipant() {
        this.participantCount++;
    }

    /**
     * 호스트 권한을 새로운 멤버에게 위임합니다.
     */
    public void transferHost(Member newHost) {
        this.hostId = newHost.getId();
        this.hostName = newHost.getName();
    }
}