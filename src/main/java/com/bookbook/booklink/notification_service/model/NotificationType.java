package com.bookbook.booklink.notification_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "알림 유형")
public enum NotificationType {

    @Schema(description = "반납 기한 알림")
    DUE_DATE_REMINDER("반납일이 다가오고 있습니다. 기한 내에 반납해주세요."),

    @Schema(description = "예약 가능 알림")
    RESERVATION_AVAILABLE("예약하신 도서가 대여 가능해졌습니다."),

    @Schema(description = "대여 신청 알림")
    LOAN_REQUEST("새로운 대여 신청이 있습니다."),

    @Schema(description = "대여 완료 알림")
    LOAN_COMPLETED("대여가 완료되었습니다."),

    @Schema(description = "댓글 알림")
    COMMENT("새로운 댓글이 등록되었습니다.");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

}
