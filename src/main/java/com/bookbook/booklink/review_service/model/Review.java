package com.bookbook.booklink.review_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.review_service.model.dto.request.ReviewCreateDto;
import com.bookbook.booklink.review_service.model.dto.request.ReviewUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 엔티티 정보")
public class Review {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(
            description = "리뷰 고유 ID (UUID)",
            example = "510e8440-eb9b-11d4-aa16-424651640000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @Column(nullable = false)
    @Min(value = 0, message = "별점은 최소 0점입니다.")
    @Max(value = 5, message = "별점은 최대 5점입니다.")
    @Schema(
            description = "별점 범위: 0~5 단위: 1",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Short rating;

    @Column(length = 100)
    @Size(max = 100, message = "한줄평은 최대 100자까지 입력 가능합니다.")
    @Schema(
            description = "리뷰 한줄평",
            example = "정말 친절한 분이에요~!",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String comment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Schema(
            description = "리뷰 생성 일자",
            example = "2025-09-22T23:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(
            description = "리뷰 대상 유형 (USER, LIBRARY)",
            example = "LIBRARY",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TargetType targetType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @Schema(
            description = "리뷰 작성자 ID",
            example = "510e8440-eb9b-11d4-aa16-424651640000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Member reviewer;

    @Column(nullable = false)
    @NotNull(message = "리뷰 대상 ID는 필수입니다.")
    @Schema(
            description = "리뷰 대상자 ID (도서관 또는 유저)",
            example = "510e8440-eb9b-11d4-aa16-424651640000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID targetId;

    // TODO: 리뷰할 거래 연결 (Loan 등)

    /**
     * DTO → Entity 변환
     */
    public static Review toEntity(ReviewCreateDto createDto, Member reviewer) {

        return Review.builder()
                .rating(createDto.getRating())
                .comment(createDto.getComment())
                .targetType(createDto.getTargetType())
                .reviewer(reviewer)
                .targetId(createDto.getTargetId())
                .build();
    }

    /**
     * 리뷰 업데이트
     */
    public void updateReview(ReviewUpdateDto updateDto) {
        this.rating = updateDto.getRating();
        this.comment = updateDto.getComment();
    }

}
