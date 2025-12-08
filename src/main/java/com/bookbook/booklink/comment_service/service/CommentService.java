package com.bookbook.booklink.comment_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.service.MemberService;
import com.bookbook.booklink.board_service.model.Board;
import com.bookbook.booklink.board_service.service.BoardService;
import com.bookbook.booklink.comment_service.model.Comment;
import com.bookbook.booklink.comment_service.model.CommentLikes;
import com.bookbook.booklink.comment_service.model.dto.request.CommentCreateDto;
import com.bookbook.booklink.comment_service.model.dto.request.CommentUpdateDto;
import com.bookbook.booklink.comment_service.model.dto.response.CommentDto;
import com.bookbook.booklink.comment_service.repository.CommentLikesRepository;
import com.bookbook.booklink.comment_service.repository.CommentRepository;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final BoardService boardService;
    private final MemberService memberService;
    private final IdempotencyService idempotencyService;

    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    /**
     * 새로운 댓글(최상위 댓글 또는 대댓글)을 생성하고 저장합니다.
     * 멱등성(Idempotency) 처리를 위해 traceId를 기반으로 키를 생성하고 중복 실행을 방지합니다.
     * <p>
     * - 대댓글인 경우 부모 댓글의 존재 여부와 2단계 이상의 대댓글 생성을 검증합니다.
     * - 부모 댓글의 자식 목록에 새 댓글을 추가하고, 부모 댓글의 댓글 수를 증가시킵니다.
     * - 댓글 생성 후 게시글의 전체 댓글 수를 증가시킵니다.
     *
     * @param dto     댓글 생성에 필요한 데이터 (게시글 ID, 부모 댓글 ID, 내용)
     * @param member  댓글을 작성하는 사용자 정보
     * @param traceId 요청의 고유 ID (멱등성 키 생성에 사용됨)
     * @return 생성된 댓글의 고유 ID (UUID)
     * @throws CustomException PARENT_COMMENT_NOT_FOUND 부모 댓글 ID가 있으나 해당 댓글이 존재하지 않을 경우
     * @throws CustomException TOO_MANY_PARENT 부모 댓글이 이미 대댓글일 경우 (2단계 이상의 대댓글 방지)
     * @throws CustomException BOARD_NOT_FOUND 댓글을 작성할 게시글이 존재하지 않을 경우
     */
    @Transactional
    public UUID createComment(CommentCreateDto dto, Member member, String traceId) {
        String key = idempotencyService.generateIdempotencyKey("comment:create", traceId);

        log.info("[CommentService] [traceId={}, userId={}] create comment initiate, boardId={}, parentId={}",
                traceId, member.getId(), dto.getBoardId(), dto.getParentId());

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        Board commentedBoard = boardService.getBoardById(dto.getBoardId());

        Comment parentComment = null;
        if (dto.getParentId() != null) {
            parentComment = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
            if (parentComment.getParent() != null) {
                throw new CustomException(ErrorCode.TOO_MANY_PARENT);
            }
        }

        Comment newComment = Comment.toEntity(dto, member, commentedBoard, parentComment);
        Comment savedComment = commentRepository.save(newComment);

        if (parentComment != null) {
            parentComment.getChildren().add(savedComment);
            parentComment.comment();
            commentRepository.save(parentComment);
        }

        boardService.commentBoard(dto.getBoardId());

        log.info("[CommentService] [traceId={}, userId={}] create comment success, commentId={}",
                traceId, member.getId(), savedComment.getId());

        return savedComment.getId();
    }

    /**
     * 기존 댓글의 내용을 수정합니다.
     * 멱등성(Idempotency) 처리를 위해 traceId를 기반으로 키를 생성하고 중복 실행을 방지합니다.
     *
     * @param commentUpdateDto 댓글 ID와 수정할 내용을 담고 있는 DTO
     * @param member           댓글을 수정하는 사용자 정보
     * @param traceId          요청의 고유 ID (멱등성 키 생성에 사용됨)
     * @return 수정된 댓글의 고유 ID (UUID)
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 삭제된 댓글일 경우
     */
    @Transactional
    public UUID updateComment(CommentUpdateDto commentUpdateDto, Member member, String traceId) {
        String key = idempotencyService.generateIdempotencyKey("comment:update", traceId);

        log.info("[CommentService] [traceId={}, userId={}] update comment initiate, commentId={}",
                traceId, member.getId(), commentUpdateDto.getId());

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        Comment existingComment = getCommentById(commentUpdateDto.getId());

        existingComment.update(commentUpdateDto);
        Comment savedComment = commentRepository.save(existingComment);

        log.info("[CommentService] [traceId={}, userId={}] update comment success, commentId={}",
                traceId, member.getId(), savedComment.getId());

        return savedComment.getId();
    }

    /**
     * 댓글을 논리적으로 삭제 처리합니다 (Soft Delete).
     * <p>
     * - 해당 댓글의 `deletedAt` 필드에 시간을 기록하여 삭제 상태로 변경합니다.
     * - 해당 댓글에 달린 모든 대댓글(children)도 함께 논리적으로 삭제 처리합니다.
     * - 댓글이 속한 게시글, 부모 댓글의 댓글 수를 1 감소시킵니다.
     *
     * @param commentId 삭제할 댓글의 고유 ID
     * @param userId    댓글을 삭제하는 사용자 ID (로깅용)
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 이미 삭제된 댓글일 경우
     */
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        log.info("[CommentService] [userId={}] delete comment initiate, commentId={}",
                userId, commentId);

        Comment comment = getCommentById(commentId);

        comment.delete();
        if (comment.getParent() != null) {
            comment.getParent().uncomment();
        }

        comment.getChildren().forEach(Comment::delete);

        Board board = comment.getBoard();
        board.uncomment();

        log.info("[CommentService] [userId={}] delete comment success, commentId={}",
                userId, commentId);
    }

    /**
     * 특정 댓글에 '좋아요'를 추가합니다.
     * 이미 '좋아요'를 누른 댓글인 경우 예외를 발생시킵니다.
     *
     * @param commentId '좋아요'를 누를 댓글의 고유 ID
     * @param member    '좋아요'를 요청한 사용자 정보
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 삭제된 댓글일 경우
     * @throws CustomException COMMENT_ALREADY_LIKES 이미 해당 댓글에 '좋아요'를 눌렀을 경우
     */
    @Transactional
    public void likeComment(UUID commentId, Member member) {
        log.info("[CommentService] [userId={}] like comment initiate, commentId={}",
                member.getId(), commentId);

        Comment comment = getCommentById(commentId);

        if (commentLikesRepository.existsByCommentAndUserId(comment, member.getId())) {
            throw new CustomException(ErrorCode.COMMENT_ALREADY_LIKES);
        }

        CommentLikes newLikes = CommentLikes.create(comment, member.getId());
        comment.getLikesList().add(newLikes);
        comment.like();

        log.info("[CommentService] [userId={}] like comment success, commentId={}", member.getId(), commentId);
    }

    /**
     * 특정 댓글에 눌렀던 '좋아요'를 취소합니다.
     * '좋아요' 기록이 없는 경우 예외를 발생시킵니다.
     *
     * @param commentId '좋아요'를 취소할 댓글의 고유 ID
     * @param member    '좋아요' 취소를 요청한 사용자 정보
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 삭제된 댓글일 경우
     * @throws CustomException COMMENT_NOT_LIKED 해당 댓글에 '좋아요' 기록이 없을 경우
     */
    @Transactional
    public void unlikeComment(UUID commentId, Member member) {
        log.info("[CommentService] [userId={}] unlike comment initiate, commentId={}", member.getId(), commentId);

        Comment comment = getCommentById(commentId);

        CommentLikes existingLike = commentLikesRepository.findByCommentAndUserId(comment, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_LIKED));

        comment.unlike();
        comment.getLikesList().remove(existingLike);

        log.info("[CommentService] [userId={}] unlike comment success, commentId={}", member.getId(), commentId);
    }

    /**
     * 특정 게시글에 속한 최상위 레벨의 댓글 목록을 조회합니다.
     * 삭제되지 않은 댓글만 포함하며, 작성일 기준 오름차순으로 정렬합니다.
     * 각 댓글에 대해 현재 사용자의 '좋아요' 여부 정보를 포함하는 DTO로 변환하여 반환합니다.
     *
     * @param boardId 댓글을 조회할 게시글의 고유 ID
     * @param member  현재 요청을 보낸 사용자 정보 (좋아요 여부 판단에 사용)
     * @return 게시글의 최상위 댓글 목록 DTO
     */
    public List<CommentDto> getCommentsByBoard(UUID boardId, Member member) {
        List<Comment> topLevelComments = commentRepository
                .findByBoardIdAndParentIsNullAndDeletedAtIsNullOrderByCreatedAtAsc(boardId);

        return topLevelComments.stream()
                .map(c -> CommentDto.fromEntity(c, member.getId()))
                .toList();
    }

    /**
     * 특정 댓글에 달린 대댓글 목록을 조회합니다.
     * 삭제되지 않은 대댓글만 포함하며, 현재 사용자의 '좋아요' 여부 정보를 포함하는 DTO로 변환하여 반환합니다.
     *
     * @param commentId 대댓글을 조회할 부모 댓글의 고유 ID
     * @param member    현재 요청을 보낸 사용자 정보 (좋아요 여부 판단에 사용)
     * @return 부모 댓글의 대댓글 목록 DTO
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 삭제된 댓글일 경우
     */
    public List<CommentDto> getCommentsByComment(UUID commentId, Member member) {
        Comment comment = getCommentById(commentId);

        return comment.getChildren()
                .stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(c -> CommentDto.fromEntity(c, member.getId())).toList();
    }

    /**
     * 댓글 ID를 사용하여 댓글 엔티티를 조회합니다.
     * 해당 ID의 댓글이 존재하지 않거나 이미 삭제된 댓글이면 예외를 발생시킵니다.
     *
     * @param commentId 조회할 댓글의 고유 ID
     * @return 조회된 Comment 엔티티
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 삭제된 댓글일 경우
     */
    public Comment getCommentById(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (comment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.COMMENT_DELETED);
        }
        return comment;
    }

    /**
     * 주어진 댓글의 작성자가 현재 사용자인지 확인합니다.
     * 이 메서드는 주로 {@code @PreAuthorize}를 통해 권한을 검증하는 데 사용됩니다.
     *
     * @param commentId 확인할 댓글의 고유 ID
     * @param member    현재 요청을 보낸 사용자 정보
     * @return 댓글의 작성자가 현재 사용자와 동일하면 true, 아니면 false
     * @throws CustomException COMMENT_NOT_FOUND 해당 ID의 댓글이 존재하지 않을 경우
     * @throws CustomException COMMENT_DELETED 삭제된 댓글일 경우
     */
    public Boolean isOwner(UUID commentId, Member member) {
        Comment comment = getCommentById(commentId);
        Member commentOwner = memberService.getMemberOrThrow(comment.getWriterId());
        return commentOwner.getId().equals(member.getId());
    }
}