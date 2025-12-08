package com.bookbook.booklink.point_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "포인트 교환 응답 DTO - 리워드 코드 교환 후의 포인트 잔액 및 교환 코드를 전달하는 객체")
public class PointExchangeDto {

    @Schema(
            description = "교환 후의 포인트 잔액",
            example = "20000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    public Integer balance;

    @Schema(
            description = "교환된 리워드 코드",
            example = "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    public UUID rewardCode;
}
