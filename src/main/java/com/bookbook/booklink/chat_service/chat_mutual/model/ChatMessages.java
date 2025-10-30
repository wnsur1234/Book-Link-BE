package com.bookbook.booklink.chat_service.chat_mutual.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.code.MessageStatus;
import com.bookbook.booklink.chat_service.chat_mutual.code.MessageType;
import com.bookbook.booklink.chat_service.chat_mutual.code.RoomType;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
public class ChatMessages {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "메시지 UUID")
    private UUID id;

    @Lob
    @Schema(description = "메시지 본문", example = "안녕하세요!")
    private String text;

    @CreationTimestamp
    @Schema(description = "전송 시각")
    private LocalDateTime sentAt;

    @Builder.Default
    @Column(nullable = false)
    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted=false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "메시지 상태", example = "SENT")
    private MessageStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "메시지 타입", example = "TEXT")
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "채팅방 타입", example = "SINGLE")
    private RoomType roomType;

    @Schema(description = "소속 채팅방 ID")
    private UUID chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MessageAttachments> attachments = new ArrayList<>();

    public static ChatMessages saveMessage(Member sender, MessageReqDto dto) {
        // 1. 먼저 메시지 엔티티 생성
        ChatMessages message = ChatMessages.builder()
                .chatId(dto.getChatId())
                .sender(sender)
                .text(dto.getText())
                .status(MessageStatus.SENT)
                .type(MessageType.TEXT)
                .roomType(RoomType.SINGLE)
                .sentAt(LocalDateTime.now())
                .build();
        // 2. 첨부파일 있으면 엔티티로 변환 후 message에 연결
        if (dto.getAttachments() != null) {
            List<MessageAttachments> attachmentEntities = dto.getAttachments().stream()
                    .map(a -> MessageAttachments.builder()
                            .fileName(a.getFileName())
                            .filePath(a.getFilePath())
                            .fileSize(a.getFileSize())
                            .message(message)
                            .build())
                    .toList();
            // ChatMessages <-> MessageAttachments 양방향 관계 설정
            message.getAttachments().addAll(attachmentEntities);
        }
        return message;
    }
}
