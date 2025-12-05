package com.bookbook.booklink.chat_service.chat_mutual.model.dto.request;

import com.bookbook.booklink.chat_service.chat_mutual.code.MessageType;
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
public class MessageReqDto {

    @Schema(description = "채팅방 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID chatId;

    @Schema(description = "메시지 본문", example = "안녕하세요!")
    private String text;

    @Schema(description = "첨부 파일 경로", example = "https://s3.bucket.com/file.png")
    private List<FileAttachmentDto> attachments;

    @Schema(description = "메세지의 콘텐츠 종류를 정의합니다.", example = "시스템에서 보낸 메세지")
    private MessageType type;
}