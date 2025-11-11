package com.bookbook.booklink.chat_service.single.model;

import com.bookbook.booklink.chat_service.chat_mutual.code.ChatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user1Id", "user2Id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleChats {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "채팅방 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "채팅방 상태", example = "ACTIVE")
    private ChatStatus status;

    @Schema(description = "마지막 메시지 내용", example = "감사합니다.")
    private String lastMessage;

    @Schema(description = "마지막 메시지 전송 시각", example = "2025-09-28T15:30:00")
    private LocalDateTime lastSentAt;

    @CreationTimestamp
    @Schema(description = "생성 시각", example = "2025-09-28T15:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "생성자 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID user1Id;

    @Schema(description = "참여자 ID", example = "7fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID user2Id;

    public static SingleChats createNormalized(UUID a, UUID b) {
        UUID u1 = a.compareTo(b) <= 0 ? a : b;
        UUID u2 = a.compareTo(b) <= 0 ? b : a;
        return SingleChats.builder()
                .user1Id(u1)
                .user2Id(u2)
                .status(ChatStatus.ACTIVE)
                .build();
    }

    public boolean hasMember(UUID memberId) {
        return memberId.equals(user1Id) || memberId.equals(user2Id);
    }

    public void updateLastMessage(String message, LocalDateTime sentAt) {
        this.lastMessage = message;
        this.lastSentAt = sentAt;
    }
}
    