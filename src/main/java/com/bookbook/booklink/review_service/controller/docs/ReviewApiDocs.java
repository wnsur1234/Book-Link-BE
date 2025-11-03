package com.bookbook.booklink.review_service.controller.docs;


import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.review_service.model.dto.request.ReviewCreateDto;
import com.bookbook.booklink.review_service.model.dto.request.ReviewUpdateDto;
import com.bookbook.booklink.review_service.model.dto.response.RatingDto;
import com.bookbook.booklink.review_service.model.dto.response.ReviewListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/review")
@Tag(name = "Review API", description = "리뷰 등록/조회/수정 관련 API")
public interface ReviewApiDocs {


    @Operation(
            summary = "리뷰 생성",
            description = "사용자/도서관에 대한 리뷰를 생성합니다. " +
                    "하나의 거래(도서 대여)당 하나의 리뷰가 가능합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PostMapping
    public ResponseEntity<BaseResponse<Boolean>> createReview(
            @Valid @RequestBody ReviewCreateDto reviewCreateDto,
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "리뷰 수정",
            description = "사용자/도서관에 대한 리뷰를 수정합니다. 평점, 코멘트 수정가능"
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.REVIEW_NOT_FOUND})
    @PutMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<Boolean>> updateReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal(expression = "member") Member member,
            @Valid @RequestBody ReviewUpdateDto reviewUpdateDto,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "리뷰 삭제",
            description = "사용자/도서관에 대한 리뷰를 삭제합니다. "
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.REVIEW_NOT_FOUND})
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<Boolean>> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "도서관에 달린 모든 리뷰 조회",
            description = "특정 도서관에 달린 모든 리뷰의 목록을 조회합니다. "
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping("/{libraryId}")
    public ResponseEntity<BaseResponse<List<ReviewListDto>>> getLibraryReview(
            @PathVariable UUID libraryId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "도서관/사용자의 평균 별점 조회",
            description = "도서관/사용자의 평균 별점을 조회합니다. " +
                    "리뷰가 하나도 없을 경우, null을 반환합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping("/rating/{targetId}")
    public ResponseEntity<BaseResponse<RatingDto>> getAvgRating(
            @PathVariable UUID targetId
    );

    @Operation(
            summary = "내 리뷰 조회",
            description = "사용자가 도서관에 대해 작성한 리뷰 목록을 조회합니다. "
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping("/my")
    ResponseEntity<BaseResponse<List<ReviewListDto>>> getMyReview(
            @AuthenticationPrincipal(expression = "member") Member member
    );
}
