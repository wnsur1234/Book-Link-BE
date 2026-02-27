package com.bookbook.booklink.notification_service.model.dto.request;

import com.bookbook.booklink.notification_service.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "알림 생성 요청 DTO")
public class NotificationCreateDto {

    @NotNull(message = "알림 수신자 ID는 필수입니다.")
    @Schema(description = "알림 수신자(회원) ID", example = "2d5f90aa-1c4d-4c63-97bb-4cf33a1a52b0", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID userId;

    @NotNull(message = "알림 유형은 필수입니다.")
    @Schema(description = "알림 유형", example = "RETURN_DUE", requiredMode = Schema.RequiredMode.REQUIRED)
    private NotificationType type;

    @Schema(description = "알림과 연관된 엔티티 ID (예: 대출 ID, 주문 ID)", example = "fa1a9db3-93c7-4f5d-a112-63c8b06b7b21")
    private UUID relatedId;
}
