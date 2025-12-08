package com.bookbook.booklink.review_service.model.dto.response;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.review_service.model.Review;
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
@Schema(description = "리뷰 응답 DTO")
public class ReviewListDto {
    @Schema(description = "리뷰 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID reviewId;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;

    @Schema(description = "작성자 닉네임", example = "nicknick")
    private String nickname;

    @Schema(description = "별점", example = "4")
    private Short rating;

    @Schema(description = "코멘트", example = "전체적으로 책 상태가 깔끔해요!")
    private String comment;

    @Schema(description = "작성 시간", example = "2025-09-23T20:05:00")
    private LocalDateTime createdAt;

    public static ReviewListDto fromEntity(Review review) {
        Member reviewer = review.getReviewer();
        return ReviewListDto.builder()
                .reviewId(review.getId())
                .profileImage(reviewer.getProfileImage())
                .nickname(reviewer.getNickname())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreateAt())
                .build();
    }
}
