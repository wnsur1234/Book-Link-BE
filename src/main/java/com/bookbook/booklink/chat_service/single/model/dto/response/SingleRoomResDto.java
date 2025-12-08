package com.bookbook.booklink.chat_service.single.model.dto.response;

import com.bookbook.booklink.chat_service.chat_mutual.code.ChatStatus;
import com.bookbook.booklink.chat_service.single.model.SingleChats;
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
// 1대1 채팅방 생성 or 기존 채팅방 응답
public class SingleRoomResDto {
    @Schema(description = "채팅방 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID chatId;

    @Schema(description = "참여자 1 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID user1Id;

    @Schema(description = "참여자 2 ID", example = "7fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID user2Id;

    @Schema(description = "마지막 메시지", example = "감사합니다.")
    private String lastMessage;

    @Schema(description = "마지막 메시지 전송 시각", example = "2025-09-28T15:30:00")
    private LocalDateTime lastSentAt;

    @Schema(description = "생성 시각", example = "2025-09-28T15:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "채팅방 상태", example = "ACTIVE")
    private ChatStatus status;


    public static SingleRoomResDto fromEntity(SingleChats chat) {
        return SingleRoomResDto.builder()
                .chatId(chat.getId())
                .user1Id(chat.getUser1Id())
                .user2Id(chat.getUser2Id())
                .lastMessage(chat.getLastMessage())
                .lastSentAt(chat.getLastSentAt())
                .createdAt(chat.getCreatedAt())
                .status(chat.getStatus())
                .build();
    }
}
