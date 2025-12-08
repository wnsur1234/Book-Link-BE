package com.bookbook.booklink.community.group_service.model.dto.response;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.community.group_service.model.GroupMember;
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
@Schema(description = "모임 참가자 목록 조회를 위한 간략 DTO")
public class ParticipantMemberListDto {

    @Schema(description = "사용자 고유 식별 ID", example = "f8a4b6c2-d1e0-4f5a-b9c8-7d6e5f4a3b21")
    private UUID id;

    @Schema(description = "사용자 이름 또는 닉네임", example = "독서왕_김철수")
    private String name;

    public static ParticipantMemberListDto fromEntity(GroupMember groupMember) {
        Member member = groupMember.getMember();
        return ParticipantMemberListDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }

    public static ParticipantMemberListDto fromEntity(Member member) {
        return ParticipantMemberListDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }
}