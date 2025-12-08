package com.bookbook.booklink.payment_service.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 상태 유형 (READY, PENDING, APPROVED, CANCEL, REJECTED)")
public enum PaymentStatus {
    READY,
    PENDING,
    APPROVED,
    CANCEL,
    REJECTED
}
