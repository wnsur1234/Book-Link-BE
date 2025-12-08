package com.bookbook.booklink.point_service.model.dto.request;

import com.bookbook.booklink.point_service.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "포인트 사용 요청 DTO - 회원의 포인트 사용(차감)을 위한 데이터 전송 객체")
public class PointUseDto {

    @NotNull(message = "거래 금액은 필수입니다.")
    @Min(value = 1, message = "거래 금액은 최소 1 이상이어야 합니다.")
    @Schema(
            description = "거래 금액 (포인트 단위)",
            example = "5000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer amount;

    @NotNull(message = "거래 유형은 필수입니다.")
    @Schema(
            description = "거래 유형 (예: CHARGE, USE, EXCHANGE, REFUND)",
            example = "USE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TransactionType type;
}
