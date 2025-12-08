package com.bookbook.booklink.review_service.model;

import com.bookbook.booklink.review_service.model.dto.request.ReviewCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 집계 정보 엔티티")
public class ReviewSummary {

    @Id
    @Schema(description = "리뷰 대상 ID (도서관 또는 사용자)", example = "510e8440-eb9b-11d4-aa16-424651640000")
    private UUID targetId;

    @Schema(description = "리뷰 대상 유형", example = "LIBRARY")
    private TargetType targetType;

    @Column(nullable = false)
    @Schema(description = "리뷰 총 개수", example = "10")
    private Integer totalCount;

    @Column(nullable = false)
    @Schema(description = "리뷰 총 별점 합", example = "42")
    private Long totalRating;

    @Column(nullable = false)
    @Schema(description = "평균 별점", example = "4.2")
    private Double avgRating;

    @UpdateTimestamp
    @Schema(description = "집계 정보 업데이트 일자", example = "2025-09-22T23:00:00")
    private LocalDateTime updatedAt;

    public static ReviewSummary toEntity(ReviewCreateDto reviewCreateDto, long totalRating, double avgRating) {
        return ReviewSummary.builder()
                .targetId(reviewCreateDto.getTargetId())
                .targetType(reviewCreateDto.getTargetType())
                .totalCount(1)
                .totalRating(totalRating)
                .avgRating(avgRating)
                .build();
    }

    public void addReview(int rating) {
        this.totalCount++;
        this.totalRating += rating;
        this.avgRating = (double) totalRating / totalCount;
    }

    public void updateReview(int oldRating, int newRating) {
        this.totalRating = this.totalRating - oldRating + newRating;
        this.avgRating = (double) totalRating / totalCount;
    }

    public void removeReview(int rating) {
        this.totalCount--;
        this.totalRating -= rating;
        this.avgRating = totalCount > 0 ? (double) totalRating / totalCount : 0.0;
    }
}
