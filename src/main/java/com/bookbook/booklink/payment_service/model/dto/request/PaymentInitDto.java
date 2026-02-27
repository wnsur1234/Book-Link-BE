package com.bookbook.booklink.payment_service.model.dto.request;

import com.bookbook.booklink.payment_service.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "결제 초기화를 위한 요청 데이터")
public class PaymentInitDto {

    @NotNull(message = "결제 ID는 필수 입력값입니다.")
    @Size(max = 100, message = "결제 ID는 100자를 초과할 수 없습니다.")
    @Schema(
            description = "결제 시스템에서 발급된 고유 ID",
            example = "order_1a2b3c4d5e",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String paymentId;

    @NotNull(message = "금액은 필수 입력값입니다.")
    @Min(value = 0, message = "금액은 0 이상이어야 합니다.")
    @Schema(
            description = "결제 금액",
            example = "15000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer amount;

    @NotNull(message = "결제 수단은 필수 입력값입니다.")
    @Schema(
            description = "결제 수단",
            example = "CARD",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private PaymentMethod method;
}
