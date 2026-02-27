package com.bookbook.booklink.review_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewUpdateDto {

    @Schema(
            description = "별점 범위: 0~5 단위: 1",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Min(0)
    @Max(5)
    @NotNull
    private Short rating;

    @Schema(
            description = "리뷰 한줄평",
            example = "정말 친절한 분이에요~!",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Size(max = 100)
    private String comment;
}
