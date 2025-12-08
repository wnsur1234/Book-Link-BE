package com.bookbook.booklink.comment_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.comment_service.controller.docs.CommentApiDocs;
import com.bookbook.booklink.comment_service.model.dto.request.CommentCreateDto;
import com.bookbook.booklink.comment_service.model.dto.request.CommentUpdateDto;
import com.bookbook.booklink.comment_service.model.dto.response.CommentDto;
import com.bookbook.booklink.comment_service.service.CommentService;
import com.bookbook.booklink.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class CommentController implements CommentApiDocs {

    private final CommentService commentService;

    @Override
    public ResponseEntity<BaseResponse<UUID>> createComment(
            @Valid @RequestBody CommentCreateDto dto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[CommentController] [traceId={}, userId={}] create comment request, boardId={}, parentId={}",
                traceId, userId, dto.getBoardId(), dto.getParentId());

        UUID newCommentId = commentService.createComment(dto, member, traceId);

        log.info("[CommentController] [traceId={}, userId={}] create comment success, commentId={}",
                traceId, userId, newCommentId);

        return ResponseEntity.ok(BaseResponse.success(newCommentId));
    }

    @Override
    @PreAuthorize("@commentService.isOwner(#dto.id,#member)")
    public ResponseEntity<BaseResponse<UUID>> updateComment(
            @RequestHeader("Trace-Id") String traceId,
            @Valid @RequestBody CommentUpdateDto dto,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[CommentController] [traceId={}, userId={}] update comment request, commentId={}",
                traceId, userId, dto.getId());

        UUID updatedId = commentService.updateComment(dto, member, traceId);

        log.info("[CommentController] [traceId={}, userId={}] update comment success, commentId={}",
                traceId, userId, updatedId);

        return ResponseEntity.ok(BaseResponse.success(updatedId));
    }

    @Override
    @PreAuthorize("@commentService.isOwner(#id,#member)")
    public ResponseEntity<BaseResponse<Boolean>> deleteComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[CommentController] [userId={}] delete comment request, commentId={}", userId, id);

        commentService.deleteComment(id, userId);

        log.info("[CommentController] [userId={}] delete comment success, commentId={}", userId, id);

        return ResponseEntity.ok(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<List<CommentDto>>> getCommentsByBoard(
            @PathVariable UUID boardId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        List<CommentDto> comments = commentService.getCommentsByBoard(boardId, member);
        return ResponseEntity.ok(BaseResponse.success(comments));
    }

    @Override
    public ResponseEntity<BaseResponse<List<CommentDto>>> getReplies(
            @PathVariable UUID parentId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        List<CommentDto> comments = commentService.getCommentsByComment(parentId, member);
        return ResponseEntity.ok(BaseResponse.success(comments));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> likeComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[CommentController] [userId={}] like comment request, commentId={}", userId, id);

        commentService.likeComment(id, member);

        log.info("[CommentController] [userId={}] like comment success, commentId={}", userId, id);

        return ResponseEntity.ok(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> unlikeComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[CommentController] [userId={}] unlike comment request, commentId={}", userId, id);

        commentService.unlikeComment(id, member);

        log.info("[CommentController] [userId={}] unlike comment success, commentId={}", userId, id);

        return ResponseEntity.ok(BaseResponse.success(Boolean.TRUE));
    }
}
