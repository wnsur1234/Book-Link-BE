package com.bookbook.booklink.chat_service.chat_mutual.model.dto.response;

import com.bookbook.booklink.chat_service.chat_mutual.code.MessageStatus;
import com.bookbook.booklink.chat_service.chat_mutual.code.MessageType;
import com.bookbook.booklink.chat_service.chat_mutual.model.ChatMessages;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.FileAttachmentDto;
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
@AllArgsConstructor
@NoArgsConstructor
public class MessageResDto {

    @Schema(description = "채팅방 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID chatId;

    @Schema(description = "보낸 사람 ID", example = "7fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID senderId;

    @Schema(description = "보낸 사람 이메일", example = "test1@exampl.com")
    private String senderEmail;

    @Schema(description = "메시지 본문", example = "안녕하세요!")
    private String text;

    @Schema(description = "전송 시각", example = "2025-09-28T15:30:00")
    private LocalDateTime sentAt;

    @Schema(description = "메시지 상태", example = "SENT")
    private MessageStatus status;

    @Schema(description = "메시지 타입", example = "TEXT")
    private MessageType type;

    @Schema(description = "첨부 파일 URL", example = "https://s3.bucket.com/file.png")
    private List<FileAttachmentDto> attachments;

    public static MessageResDto fromEntity(ChatMessages entity) {
        return MessageResDto.builder()
                .chatId(entity.getChatId())
                .senderId(entity.getSender().getId())
                .senderEmail(entity.getSender().getEmail())
                .text(entity.getText())
                .sentAt(entity.getSentAt())
                .status(entity.getStatus())
                .type(entity.getType())
                .attachments(
                        entity.getAttachments() == null ?
                                List.of() :
                                entity.getAttachments().stream()
                                        .map(a -> new FileAttachmentDto(a.getFileName(), a.getFilePath(), a.getFileSize()))
                                        .toList()
                )
                .build();
    }
}
