package com.bookbook.booklink.community.group_service.model.dto.response;

import com.bookbook.booklink.community.group_service.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "모임 목록 조회를 위한 간략 응답 DTO")
public class GroupListDto {

    @Schema(description = "모임의 고유 식별 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;

    @Schema(description = "그룹 썸네일 이미지 URL 또는 경로", example = "http://example.com/images/group_thumb.jpg")
    private String thumbnail;

    @Schema(description = "모임 이름", example = "주말 SF 독서 모임")
    private String name;

    @Schema(description = "현재 모임 참여 인원수", example = "15")
    private Integer participantCount;

    @Schema(description = "모임 최대 참여 인원수 (최대 30명)", example = "20")
    private Integer maxCapacity;

    @Schema(description = "비공개 여부 (true: 비공개, false: 공개)", example = "true")
    private Boolean isPrivate;

    public static GroupListDto fromEntity(Group group) {
        return GroupListDto.builder()
                .id(group.getId())
                .thumbnail(group.getThumbnail())
                .name(group.getName())
                .participantCount(group.getParticipantCount())
                .maxCapacity(group.getMaxCapacity())
                .isPrivate(group.getIsPrivate())
                .build();
    }
}