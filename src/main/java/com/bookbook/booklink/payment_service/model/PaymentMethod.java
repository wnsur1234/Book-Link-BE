package com.bookbook.booklink.payment_service.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 수단 유형 (CARD, KAKAOPAY)")
public enum PaymentMethod {
    CARD,
    KAKAOPAY
}
