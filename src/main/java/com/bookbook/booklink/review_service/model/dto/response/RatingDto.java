package com.bookbook.booklink.review_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {

    @Schema(description = "평균 별점", example = "4.1")
    private Double rating;

    @Schema(description = "리뷰어 수", example = "50")
    private Integer reviewerCount;

    public static RatingDto toDto(Double rating, Integer reviewerCount) {
        return RatingDto.builder()
                .rating(rating)
                .reviewerCount(reviewerCount)
                .build();
    }
}
