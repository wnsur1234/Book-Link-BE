package com.bookbook.booklink.review_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.review_service.controller.docs.ReviewApiDocs;
import com.bookbook.booklink.review_service.model.dto.request.ReviewCreateDto;
import com.bookbook.booklink.review_service.model.dto.request.ReviewUpdateDto;
import com.bookbook.booklink.review_service.model.dto.response.RatingDto;
import com.bookbook.booklink.review_service.model.dto.response.ReviewListDto;
import com.bookbook.booklink.review_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewApiDocs {
    private final ReviewService reviewService;

    @Override
    public ResponseEntity<BaseResponse<Boolean>> createReview(
            @Valid @RequestBody ReviewCreateDto reviewCreateDto,
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = member.getId();

        log.info("[ReviewController] [traceId = {}, userId = {}] create review request received. targetId={}",
                traceId, userId, reviewCreateDto.getTargetId());

        reviewService.createReview(reviewCreateDto, traceId, member);

        log.info("[ReviewController] [traceId = {}, userId = {}] create review response success. targetId={}",
                traceId, userId, reviewCreateDto.getTargetId());

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> updateReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal(expression = "member") Member member,
            @Valid @RequestBody ReviewUpdateDto reviewUpdateDto,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = UUID.randomUUID();

        log.info("[ReviewController] [traceId = {}, userId = {}] update review request received. reviewId={}",
                traceId, userId, reviewId);

        reviewService.updateReview(reviewUpdateDto, reviewId, traceId, member);

        log.info("[ReviewController] [traceId = {}, userId = {}] update review request success. reviewId={}",
                traceId, userId, reviewId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID traceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        log.info("[ReviewController] [traceId = {}, userId = {}] delete review request received. reviewId={}",
                traceId, userId, reviewId);

        reviewService.deleteReview(reviewId, traceId, member);

        log.info("[ReviewController] [traceId = {}, userId = {}] delete review request success. reviewId={}",
                traceId, userId, reviewId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<List<ReviewListDto>>> getLibraryReview(
            @PathVariable UUID libraryId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        List<ReviewListDto> reviewListDtoList = reviewService.getLibraryReview(libraryId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(reviewListDtoList));
    }

    @Override
    public ResponseEntity<BaseResponse<RatingDto>> getAvgRating(
            @PathVariable UUID targetId
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(reviewService.getAvgRating(targetId)));
    }

    public ResponseEntity<BaseResponse<List<ReviewListDto>>> getMyReview(
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(reviewService.getMyReview(member)));
    }
}
    