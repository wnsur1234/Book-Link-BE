package com.bookbook.booklink.comment_service.controller.docs;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.comment_service.model.dto.request.CommentCreateDto;
import com.bookbook.booklink.comment_service.model.dto.request.CommentUpdateDto;
import com.bookbook.booklink.comment_service.model.dto.response.CommentDto;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comment API", description = "댓글 등록/조회/수정 관련 API")
@RequestMapping("/api/comment")
public interface CommentApiDocs {

    @Operation(
            summary = "댓글/대댓글 등록",
            description = "게시글 또는 댓글에 새로운 댓글/대댓글을 등록합니다."
    )
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.PARENT_COMMENT_NOT_FOUND,
            ErrorCode.TOO_MANY_PARENT,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping
    ResponseEntity<BaseResponse<UUID>> createComment(
            @Valid @RequestBody CommentCreateDto dto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "댓글/대댓글 수정",
            description = "본인이 작성한 댓글/대댓글을 수정합니다."
    )
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.COMMENT_NOT_FOUND,
            ErrorCode.COMMENT_DELETED,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @PutMapping
    ResponseEntity<BaseResponse<UUID>> updateComment(
            @RequestHeader("Trace-Id") String traceId,
            @Valid @RequestBody CommentUpdateDto dto,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "댓글/대댓글 삭제 (soft delete)",
            description = "본인이 작성한 댓글/대댓글을 삭제합니다."
    )
    @ApiErrorResponses({
            ErrorCode.COMMENT_NOT_FOUND,
            ErrorCode.COMMENT_DELETED,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> deleteComment(
            @Parameter(description = "삭제할 댓글/대댓글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "게시글 댓글 목록 조회",
            description = "특정 게시글의 댓글 목록을 조회합니다. (최상위 댓글만, 삭제된 댓글 제외)"
    )
    @ApiErrorResponses({
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED,
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/{boardId}")
    ResponseEntity<BaseResponse<List<CommentDto>>> getCommentsByBoard(
            @Parameter(description = "댓글 목록을 조회할 게시글 고유 ID") @PathVariable UUID boardId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "대댓글 목록 조회",
            description = "특정 댓글에 대한 대댓글 목록을 조회합니다. (삭제된 대댓글 제외)"
    )
    @ApiErrorResponses({
            ErrorCode.COMMENT_NOT_FOUND,
            ErrorCode.COMMENT_DELETED,
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/{parentId}/replies")
    ResponseEntity<BaseResponse<List<CommentDto>>> getReplies(
            @Parameter(description = "대댓글 목록을 조회할 부모 댓글 고유 ID") @PathVariable UUID parentId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "댓글 좋아요",
            description = "특정 댓글에 좋아요를 누릅니다."
    )
    @ApiErrorResponses({
            ErrorCode.COMMENT_NOT_FOUND,
            ErrorCode.COMMENT_DELETED,
            ErrorCode.COMMENT_ALREADY_LIKES,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping("/{id}/like")
    ResponseEntity<BaseResponse<Boolean>> likeComment(
            @Parameter(description = "좋아요를 누를 댓글/대댓글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "댓글 좋아요 취소",
            description = "특정 댓글의 좋아요를 취소합니다."
    )
    @ApiErrorResponses({
            ErrorCode.COMMENT_NOT_FOUND,
            ErrorCode.COMMENT_DELETED,
            ErrorCode.COMMENT_NOT_LIKED,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/{id}/like")
    ResponseEntity<BaseResponse<Boolean>> unlikeComment(
            @Parameter(description = "좋아요를 취소할 댓글/대댓글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );
}
