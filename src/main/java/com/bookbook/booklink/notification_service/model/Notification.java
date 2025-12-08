package com.bookbook.booklink.notification_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.notification_service.model.dto.request.NotificationCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "알림(Notification) 엔티티")
public class Notification {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(
            description = "알림 고유 식별자",
            example = "c3c9c5d4-52a1-4a9d-93c8-9f0a1a2f6f6d",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull
    @Schema(description = "알림을 받은 사용자")
    private Member member;

    @Column(nullable = false)
    @NotBlank(message = "알림 메시지는 필수입니다.")
    @Schema(
            description = "알림 메시지",
            example = "반납일이 3일 남았습니다.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "알림 유형은 필수입니다.")
    @Schema(
            description = "알림 유형",
            example = "RETURN_DUE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private NotificationType type;

    @Column(nullable = false)
    @Schema(
            description = "알림과 연관된 엔티티의 ID (예: 대출 ID, 주문 ID)",
            example = "fa1a9db3-93c7-4f5d-a112-63c8b06b7b21",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID relatedId;

    @Column(nullable = false)
    @Schema(
            description = "알림 읽음 여부",
            example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean isRead;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @Schema(
            description = "알림 생성 일시",
            example = "2025-09-24T15:30:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createdAt;

    public static Notification toEntity(NotificationCreateDto notificationCreateDto, Member member) {
        return Notification.builder()
                .member(member)
                .message(notificationCreateDto.getType().getDefaultMessage())
                .type(notificationCreateDto.getType())
                .relatedId(notificationCreateDto.getRelatedId())
                .isRead(Boolean.FALSE)
                .build();
    }

    public void read() {
        this.isRead = Boolean.TRUE;
    }
}
