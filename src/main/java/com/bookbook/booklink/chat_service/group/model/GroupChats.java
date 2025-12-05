package com.bookbook.booklink.chat_service.group.model;

import com.bookbook.booklink.community.group_service.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupChats {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "채팅방 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @OneToOne
    private Group group;  // 모임과 1:1 연결

    @Schema(description = "마지막 메시지 내용", example = "감사합니다.")
    private String lastMessage;

    @Schema(description = "마지막 메시지 전송 시각", example = "2025-09-28T15:30:00")
    private LocalDateTime lastSentAt;

    @CreationTimestamp
    @Schema(description = "생성 시각", example = "2025-09-28T15:00:00")
    private LocalDateTime createdAt;

}
