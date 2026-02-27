package com.bookbook.booklink.point_service.model.dto.response;

import com.bookbook.booklink.point_service.model.PointHistory;
import com.bookbook.booklink.point_service.model.TransactionType;
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
@Schema(description = "포인트 거래 내역 목록 응답 DTO - 회원의 포인트 거래 내역을 전달하는 객체")
public class PointHistoryListDto {

    @Schema(
            description = "거래 내역 고유 식별자 (UUID)",
            example = "3f4a9b9b-0e3c-4e7e-8a3c-5c9f5b3d2a19",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @Schema(
            description = "거래 금액 (포인트 단위)",
            example = "5000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer amount;

    @Schema(
            description = "거래 후 잔액 (포인트 단위)",
            example = "15000",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Integer balanceAfter;

    @Schema(
            description = "거래 유형 (예: CHARGE, USE, EXCHANGE, REFUND)",
            example = "USE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TransactionType type;

    @Schema(
            description = "거래에 대한 설명",
            example = "책 구매를 위한 포인트 사용",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String description;

    @Schema(
            description = "거래 날짜",
            example = "2025-09-26T19:20:30",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createdAt;

    public static PointHistoryListDto fromEntity(PointHistory pointHistory) {
        return PointHistoryListDto.builder()
                .id(pointHistory.getId())
                .amount(pointHistory.getAmount())
                .balanceAfter(pointHistory.getBalanceAfter())
                .type(pointHistory.getType())
                .description(pointHistory.getDescription())
                .createdAt(pointHistory.getCreatedAt())
                .build();
    }
}
