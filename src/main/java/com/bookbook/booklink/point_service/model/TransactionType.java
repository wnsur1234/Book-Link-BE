package com.bookbook.booklink.point_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "포인트 거래 유형")
public enum TransactionType {

    @Schema(description = "포인트 충전 (사용자가 직접 결제하여 포인트를 충전한 경우)")
    CHARGE("포인트 충전"),

    @Schema(description = "포인트 사용 (상품 구매 등으로 포인트를 사용한 경우)")
    USE("포인트 사용"),

    @Schema(description = "포인트 환불 (결제 취소 또는 오류로 인한 환불)")
    REFUND("포인트 환불"),

    @Schema(description = "포인트 적립 (도서 반납, 활동 보상 등으로 포인트를 지급받은 경우)")
    EARN("포인트 적립"),

    @Schema(description = "이벤트 지급 (이벤트 참여 보상으로 지급된 포인트)")
    EVENT("이벤트 참여 보상"),

    @Schema(description = "상품권 전환 (포인트를 상품권으로 전환)")
    EXCHANGE("상품권 전환");

    private final String defaultDescription;

    TransactionType(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

}
