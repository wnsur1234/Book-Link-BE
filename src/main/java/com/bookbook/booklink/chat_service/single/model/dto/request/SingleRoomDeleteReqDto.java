package com.bookbook.booklink.chat_service.single.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleRoomDeleteReqDto {

    @Schema(description = "삭제할 채팅방 ID 목록. deleteAll이 true면 무시됩니다.")
    private List<UUID> chatIds;

    @Schema(description = "true 이면 내가 참여한 전체 채팅방 목록을 삭제합니다.")
    private Boolean deleteAll;
}