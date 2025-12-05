package com.bookbook.booklink.board_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.board_service.model.Board;
import com.bookbook.booklink.board_service.model.BoardCategory;
import com.bookbook.booklink.board_service.model.BoardLikes;
import com.bookbook.booklink.board_service.model.dto.request.BoardCreateDto;
import com.bookbook.booklink.board_service.model.dto.request.BoardSort;
import com.bookbook.booklink.board_service.model.dto.request.BoardUpdateDto;
import com.bookbook.booklink.board_service.model.dto.response.BoardDetailDto;
import com.bookbook.booklink.board_service.model.dto.response.BoardListDto;
import com.bookbook.booklink.board_service.repository.BoardLikesRepository;
import com.bookbook.booklink.board_service.repository.BoardRepository;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j // 👈 로그 사용을 위해 추가
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardLikesRepository boardLikesRepository;
    private final IdempotencyService idempotencyService;

    /**
     * 새로운 게시글을 생성하고 저장합니다.
     * 멱등성(Idempotency) 처리를 위해 traceId를 기반으로 키를 생성하고 중복 실행을 방지합니다.
     *
     * @param boardCreateDto 게시글 생성에 필요한 데이터 (제목, 내용, 카테고리)
     * @param member         게시글을 작성하는 사용자 정보
     * @param traceId        요청의 고유 ID (멱등성 키 생성에 사용됨)
     * @return 생성된 게시글의 고유 ID (UUID)
     */
    @Transactional
    public UUID createBoard(BoardCreateDto boardCreateDto, Member member, String traceId) {
        String key = idempotencyService.generateIdempotencyKey("board:create", traceId);

        log.info("[BoardService] [traceId={}, userId={}] create board initiate, title={}",
                traceId, member.getId(), boardCreateDto.getTitle());

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        Board newBoard = Board.toEntity(boardCreateDto, member);

        Board savedBoard = boardRepository.save(newBoard);

        log.info("[BoardService] [traceId={}, userId={}] create board success, boardId={}",
                traceId, member.getId(), savedBoard.getId());

        return savedBoard.getId();

    }

    /**
     * 기존 게시글의 제목과 내용을 수정합니다.
     * 멱등성(Idempotency) 처리를 위해 traceId를 기반으로 키를 생성하고 중복 실행을 방지합니다.
     *
     * @param boardUpdateDto 게시글 ID와 수정할 내용을 담고 있는 DTO
     * @param member         게시글을 수정하는 사용자 정보
     * @param traceId        요청의 고유 ID (멱등성 키 생성에 사용됨)
     * @return 수정된 게시글의 고유 ID (UUID)
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    @Transactional
    public UUID updateBoard(BoardUpdateDto boardUpdateDto, Member member, String traceId) {
        String key = idempotencyService.generateIdempotencyKey("board:update", traceId);

        log.info("[BoardService] [traceId={}, userId={}] update board initiate, boardId={}",
                traceId, member.getId(), boardUpdateDto.getBoardId());

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        Board existingBoard = getBoardById(boardUpdateDto.getBoardId());

        existingBoard.update(boardUpdateDto);

        Board savedBoard = boardRepository.save(existingBoard);

        log.info("[BoardService] [traceId={}, userId={}] update board success, boardId={}",
                traceId, member.getId(), savedBoard.getId());

        return savedBoard.getId();

    }

    /**
     * 게시글을 논리적으로 삭제 처리합니다 (Soft Delete).
     *
     * @param boardId 삭제할 게시글의 고유 ID
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    @Transactional
    public void deleteBoard(UUID boardId) {
        log.info("[BoardService] delete board initiate, boardId={}", boardId);

        Board board = getBoardById(boardId);

        board.delete();

        log.info("[BoardService] delete board success, boardId={}", boardId);
    }

    /**
     * 검색 조건에 맞는 게시글 목록을 조회합니다.
     *
     * @param title    검색할 제목 키워드 (null 허용)
     * @param category 검색할 게시글 카테고리 (null 허용)
     * @param sort 정렬 (default 최신순)
     * @return BoardListDto 목록
     */
    @Transactional(readOnly = true)
    public List<BoardListDto> getBoards(String title, BoardCategory category, BoardSort sort) {

        Sort order = switch (sort) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR ->  Sort.by(Sort.Direction.DESC, "likeCount");
        };

        List<Board> boardList = boardRepository.findByTitleAndCategory(title, category, order);

        return boardList.stream().map(BoardListDto::fromEntity).toList();
    }

    /**
     * 특정 게시글을 조회하고 조회수를 1 증가시킵니다.
     *
     * @param boardId 조회할 게시글의 고유 ID
     * @return BoardDetailDto 상세 정보
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    @Transactional
    public BoardDetailDto getBoard(UUID boardId, UUID userId) {
        log.info("[BoardService] get board initiate and view count up, boardId={}", boardId);

        Board board = getBoardById(boardId);

        board.view();

        log.info("[BoardService] get board success, boardId={}, viewCount={}", boardId, board.getViewCount());

        return BoardDetailDto.fromEntity(board, userId);

    }

    /**
     * 특정 게시글에 '좋아요'를 추가합니다.
     * 이미 '좋아요'를 누른 게시글인 경우 예외를 발생시킵니다.
     *
     * @param boardId '좋아요'를 누를 게시글의 고유 ID
     * @param member  '좋아요'를 요청한 사용자 정보
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     * @throws CustomException BOARD_ALREADY_LIKES 이미 해당 게시글에 '좋아요'를 눌렀을 경우
     */
    @Transactional
    public void likeBoard(UUID boardId, Member member) {
        log.info("[BoardService] [userId={}] like board initiate, boardId={}", member.getId(), boardId);

        Board board = getBoardById(boardId);

        if (boardLikesRepository.existsByBoardAndUserId(board, member.getId())) {
            throw new CustomException(ErrorCode.BOARD_ALREADY_LIKES);
        }

        board.like();
        BoardLikes newLikes = BoardLikes.create(board, member.getId());
        board.getLikesList().add(newLikes);
        boardLikesRepository.save(newLikes); // 좋아요 기록 저장

        log.info("[BoardService] [userId={}] like board success, boardId={}, likeCount={}",
                member.getId(), boardId, board.getLikeCount());
    }

    /**
     * 특정 게시글에 눌렀던 '좋아요'를 취소합니다.
     * '좋아요' 기록이 없는 경우 예외를 발생시킵니다.
     *
     * @param boardId '좋아요'를 취소할 게시글의 고유 ID
     * @param member  '좋아요' 취소를 요청한 사용자 정보
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     * @throws CustomException BOARD_NOT_LIKED 해당 게시글에 '좋아요' 기록이 없을 경우
     */
    @Transactional
    public void unlikeBoard(UUID boardId, Member member) {
        log.info("[BoardService] [userId={}] unlike board initiate, boardId={}", member.getId(), boardId);

        Board board = getBoardById(boardId);

        BoardLikes existingLike = boardLikesRepository.findByBoardAndUserId(board, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_LIKED));

        board.unlike();
        board.getLikesList().remove(existingLike);
        boardLikesRepository.delete(existingLike); // 좋아요 기록 삭제

        log.info("[BoardService] [userId={}] unlike board success, boardId={}, likeCount={}",
                member.getId(), boardId, board.getLikeCount());
    }

    /**
     * 특정 사용자가 해당 게시글에 '좋아요'를 눌렀는지 확인합니다.
     *
     * @param boardId 확인할 게시글의 고유 ID
     * @param member  현재 사용자 정보
     * @return 사용자가 해당 게시글에 좋아요를 눌렀으면 true, 아니면 false
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    @Transactional(readOnly = true)
    public boolean hasUserLikedBoard(UUID boardId, Member member) {
        Board board = getBoardById(boardId);
        return boardLikesRepository.existsByBoardAndUserId(board, member.getId());
    }

    /**
     * 댓글이 생성/삭제될 때 게시글의 댓글 수를 1 증가시킵니다.
     * 주로 CommentService에서 호출됩니다.
     *
     * @param boardId 댓글 수가 증가/감소할 게시글의 고유 ID
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    @Transactional
    public void commentBoard(UUID boardId) {
        log.info("[BoardService] comment count update initiate, boardId={}", boardId);

        Board board = getBoardById(boardId);
        board.comment();

        log.info("[BoardService] comment count update success, boardId={}, commentCount={}",
                boardId, board.getCommentCount());
    }

    /**
     * 게시글 ID를 사용하여 게시글 엔티티를 조회합니다.
     * 해당 ID의 게시글이 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param boardId 조회할 게시글의 고유 ID
     * @return 조회된 Board 엔티티
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    public Board getBoardById(UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        if (board.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.BOARD_DELETED);
        }
        return board;
    }

    /**
     * 주어진 게시글의 작성자가 현재 사용자인지 확인합니다.
     *
     * @param boardId 확인할 게시글의 고유 ID
     * @param member  현재 요청을 보낸 사용자 정보
     * @return 게시글의 작성자가 현재 사용자와 동일하면 true, 아니면 false
     * @throws CustomException BOARD_NOT_FOUND 해당 ID의 게시글이 존재하지 않을 경우
     * @throws CustomException BOARD_DELETED 삭제된 게시글일 경우
     */
    public Boolean isOwner(UUID boardId, Member member) {
        Board board = getBoardById(boardId);
        return board.getMember().getId().equals(member.getId());
    }
}