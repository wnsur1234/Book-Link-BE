package com.bookbook.booklink.point_service.model.dto.response;

import com.bookbook.booklink.point_service.model.Point;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "포인트 잔액 응답 DTO - 회원의 현재 포인트 잔액을 전달하는 객체")
public class PointBalanceDto {

    @Schema(
            description = "포인트 잔액",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    public Integer balance;

    public static PointBalanceDto fromEntity(Point point) {
        return PointBalanceDto.builder().balance(point.getBalance()).build();
    }
}
