package com.bookbook.booklink.notification_service.model.dto.response;

import com.bookbook.booklink.notification_service.model.Notification;
import com.bookbook.booklink.notification_service.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "알림 응답 DTO")
public class NotificationResDto {

    @Schema(description = "알림 고유 식별자", example = "c3c9c5d4-52a1-4a9d-93c8-9f0a1a2f6f6d", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Schema(description = "알림 메시지", example = "반납일이 3일 남았습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "알림 유형", example = "RETURN_DUE", requiredMode = Schema.RequiredMode.REQUIRED)
    private NotificationType type;

    @Schema(description = "알림과 연관된 엔티티 ID (예: 대출 ID, 주문 ID)", example = "fa1a9db3-93c7-4f5d-a112-63c8b06b7b21")
    private UUID relatedId;

    @Schema(description = "알림 읽음 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isRead;

    @Schema(description = "알림 생성 일시", example = "2025-09-24T15:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    public static NotificationResDto fromEntity(Notification notification) {
        return NotificationResDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .relatedId(notification.getRelatedId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
