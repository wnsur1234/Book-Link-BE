package com.bookbook.booklink.community.group_service.model.dto.response;

import com.bookbook.booklink.community.group_service.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "모임 상세 정보를 담는 응답 DTO")
public class GroupDetailDto {

    @Schema(description = "모임의 고유 식별 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;

    @Schema(description = "그룹 썸네일 이미지 URL 또는 경로", example = "http://example.com/images/group_thumb.jpg")
    private String thumbnail;

    @Schema(description = "모임 이름", example = "월간 독서 클럽")
    private String name;

    @Schema(description = "모임에 대한 상세 설명", example = "매월 다른 장르의 책을 읽고 심도 있는 토론을 진행합니다.")
    private String description;

    @Schema(description = "현재 모임 참여 인원수", example = "7")
    private Integer participantCount;

    @Schema(description = "모임 최대 참여 인원수 (최대 30명)", example = "10")
    private Integer maxCapacity;

    @Schema(description = "모임을 만든 주최자(호스트) 사용자 이름", example = "책읽는_곰")
    private String hostName;

    @Schema(description = "모임을 만든 주최자(호스트) 사용자 아이디(UUID)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID hostId;

    @Schema(description = "비공개 여부 (true: 비공개, false: 공개)", example = "false")
    private Boolean isPrivate;

    @Schema(description = "참여 여부 (true: 참여, false: 미참여)", example = "false")
    private Boolean isParticipant;

    @Schema(description = "모임 멤버 목록 (비공개 모임은 멤버만 조회 가능)")
    private List<ParticipantMemberListDto> memberList;

    public static GroupDetailDto fromEntity(Group group, List<ParticipantMemberListDto> memberList, Boolean isParticipant) {
        return GroupDetailDto.builder()
                .id(group.getId())
                .thumbnail(group.getThumbnail())
                .name(group.getName())
                .description(group.getDescription())
                .participantCount(group.getParticipantCount())
                .maxCapacity(group.getMaxCapacity())
                .hostName(group.getHostName())
                .hostId(group.getHostId())
                .isPrivate(group.getIsPrivate())
                .isParticipant(isParticipant)
                .memberList(memberList)
                .build();
    }
}