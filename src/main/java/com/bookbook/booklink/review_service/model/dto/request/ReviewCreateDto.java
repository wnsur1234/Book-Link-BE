package com.bookbook.booklink.review_service.model.dto.request;

import com.bookbook.booklink.review_service.model.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "리뷰 생성 요청 DTO")
public class ReviewCreateDto {

    @NotBlank(message = "리뷰 작성자 ID는 필수입니다.")
    @Schema(
            description = "리뷰 작성자 ID",
            example = "user-1234",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID reviewerId;

    @NotBlank(message = "리뷰 대상 ID는 필수입니다.")
    @Schema(
            description = "리뷰를 달 대상의 ID (도서관 또는 사용자)",
            example = "510e8440-eb9b-11d4-aa16-424651640000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID targetId;

    @NotNull(message = "리뷰 대상 타입은 필수입니다.")
    @Schema(
            description = "리뷰 대상 유형 (USER, LIBRARY)",
            example = "LIBRARY",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TargetType targetType;

    @NotNull(message = "별점은 필수입니다.")
    @Min(value = 0, message = "별점은 최소 0점입니다.")
    @Max(value = 5, message = "별점은 최대 5점입니다.")
    @Schema(
            description = "별점 범위: 0~5 단위: 1",
            example = "4",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Short rating;

    @Size(max = 100, message = "한줄평은 최대 100자까지 입력 가능합니다.")
    @Schema(
            description = "리뷰 한줄평",
            example = "정말 친절한 분이에요~!",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String comment;
}
