package com.bookbook.booklink.payment_service.model.dto.response;

import com.bookbook.booklink.payment_service.model.Payment;
import com.bookbook.booklink.payment_service.model.PaymentMethod;
import com.bookbook.booklink.payment_service.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 결과 응답 데이터")
public class PaymentResDto {

    @Schema(
            description = "결제 금액",
            example = "15000"
    )
    private Integer amount;

    @Schema(
            description = "결제 수단",
            example = "CARD"
    )
    private PaymentMethod paymentMethod;

    @Schema(
            description = "결제 상태",
            example = "APPROVED"
    )
    private PaymentStatus status;

    @Schema(
            description = "결제 생성 시각",
            example = "2025-09-26T19:20:30"
    )
    private LocalDateTime createdAt;

    public static PaymentResDto fromEntity(Payment payment) {
        return PaymentResDto.builder()
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
