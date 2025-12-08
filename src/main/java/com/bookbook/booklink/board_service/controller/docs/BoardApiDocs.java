package com.bookbook.booklink.board_service.controller.docs;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.board_service.model.BoardCategory;
import com.bookbook.booklink.board_service.model.dto.request.BoardCreateDto;
import com.bookbook.booklink.board_service.model.dto.request.BoardSort;
import com.bookbook.booklink.board_service.model.dto.request.BoardUpdateDto;
import com.bookbook.booklink.board_service.model.dto.response.BoardDetailDto;
import com.bookbook.booklink.board_service.model.dto.response.BoardListDto;
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

@Tag(name = "Board API", description = "게시글 등록/조회/수정 관련 API")
@RequestMapping("/api/board")
public interface BoardApiDocs {

    @Operation(
            summary = "게시글 등록",
            description = "새로운 게시글을 등록합니다. (인증 필요)"
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR})
    @PostMapping
    ResponseEntity<BaseResponse<UUID>> createBoard(
            @Valid @RequestBody BoardCreateDto boardCreateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "게시글 수정",
            description = "본인이 작성한 게시글의 제목과 내용을 수정합니다. (인증 및 소유자 권한 필요)"
    )
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @PutMapping
    ResponseEntity<BaseResponse<UUID>> updateBoard(
            @Valid @RequestBody BoardUpdateDto boardUpdateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "게시글 삭제 (soft delete)",
            description = "본인이 작성한 게시글을 논리적으로 삭제합니다. (인증 및 소유자 권한 필요)"
    )
    @ApiErrorResponses({
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> deleteBoard(
            @Parameter(description = "삭제할 게시글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "게시글 목록 조회",
            description = "제목 키워드나 카테고리로 필터링된 게시글 목록을 조회합니다."
    )
    @GetMapping
    ResponseEntity<BaseResponse<List<BoardListDto>>> getBoards(
            @Parameter(description = "검색할 제목 키워드", required = false) @RequestParam(required = false) String title,
            @Parameter(description = "검색할 카테고리", required = false) @RequestParam(required = false) BoardCategory category,
            @Parameter(description = "정렬 조건") @RequestParam(defaultValue = "LATEST") BoardSort sort
    );

    @Operation(
            summary = "게시글 상세 조회 및 조회수 증가",
            description = "특정 게시글의 상세 내용을 조회하고 조회수를 1 증가시킵니다."
    )
    @ApiErrorResponses({
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED
    })
    @GetMapping("/{id}")
    ResponseEntity<BaseResponse<BoardDetailDto>> getBoard(
            @Parameter(description = "조회할 게시글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "게시글 좋아요",
            description = "특정 게시글에 좋아요를 누릅니다. (인증 필요)"
    )
    @ApiErrorResponses({
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED,
            ErrorCode.BOARD_ALREADY_LIKES,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping("/{id}/like")
    ResponseEntity<BaseResponse<Boolean>> likeBoard(
            @Parameter(description = "좋아요를 누를 게시글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "게시글 좋아요 취소",
            description = "특정 게시글의 좋아요를 취소합니다. (인증 필요)"
    )
    @ApiErrorResponses({
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED,
            ErrorCode.BOARD_NOT_LIKED,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/{id}/like")
    ResponseEntity<BaseResponse<Boolean>> unlikeBoard(
            @Parameter(description = "좋아요를 취소할 게시글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "사용자 좋아요 여부 확인",
            description = "현재 인증된 사용자가 특정 게시글에 좋아요를 눌렀는지 확인합니다."
    )
    @ApiErrorResponses({
            ErrorCode.BOARD_NOT_FOUND,
            ErrorCode.BOARD_DELETED
    })
    @GetMapping("/{id}/like")
    ResponseEntity<BaseResponse<Boolean>> hasUserLikedBoard(
            @Parameter(description = "확인할 게시글 고유 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );
}