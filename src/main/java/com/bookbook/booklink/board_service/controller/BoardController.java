package com.bookbook.booklink.board_service.controller;


import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.board_service.controller.docs.BoardApiDocs;
import com.bookbook.booklink.board_service.model.BoardCategory;
import com.bookbook.booklink.board_service.model.dto.request.BoardCreateDto;
import com.bookbook.booklink.board_service.model.dto.request.BoardSort;
import com.bookbook.booklink.board_service.model.dto.request.BoardUpdateDto;
import com.bookbook.booklink.board_service.model.dto.response.BoardDetailDto;
import com.bookbook.booklink.board_service.model.dto.response.BoardListDto;
import com.bookbook.booklink.board_service.service.BoardService;
import com.bookbook.booklink.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardController implements BoardApiDocs {
    private final BoardService boardService;

    @Override
    public ResponseEntity<BaseResponse<UUID>> createBoard(
            @Valid @RequestBody BoardCreateDto boardCreateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[BoardController] [traceId={}, userId={}] create board request, title={}",
                traceId, userId, boardCreateDto.getTitle());

        UUID newBoardId = boardService.createBoard(boardCreateDto, member, traceId);

        log.info("[BoardController] [traceId={}, userId={}] create board success, boardId={}",
                traceId, userId, newBoardId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(newBoardId));
    }

    @Override
    @PreAuthorize("@boardService.isOwner(#boardUpdateDto.boardId, #member)")
    public ResponseEntity<BaseResponse<UUID>> updateBoard(
            @Valid @RequestBody BoardUpdateDto boardUpdateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[BoardController] [traceId={}, userId={}] update board request, boardId={}",
                traceId, userId, boardUpdateDto.getBoardId());

        UUID updateBoardId = boardService.updateBoard(boardUpdateDto, member, traceId);

        log.info("[BoardController] [traceId={}, userId={}] update board success, boardId={}",
                traceId, userId, updateBoardId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(updateBoardId));
    }

    @Override
    @PreAuthorize("@boardService.isOwner(#id, #member)")
    public ResponseEntity<BaseResponse<Boolean>> deleteBoard(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[BoardController] [userId={}] delete board request, boardId={}", userId, id);

        boardService.deleteBoard(id);

        log.info("[BoardController] [userId={}] delete board success, boardId={}", userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<List<BoardListDto>>> getBoards(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) BoardCategory category,
            @RequestParam(defaultValue = "LATEST") BoardSort sort
    ) {
        List<BoardListDto> dtoList = boardService.getBoards(title, category, sort);

        return ResponseEntity.ok()
                .body(BaseResponse.success(dtoList));
    }

    @Override
    public ResponseEntity<BaseResponse<BoardDetailDto>> getBoard(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        BoardDetailDto dto = boardService.getBoard(id, member.getId());

        return ResponseEntity.ok()
                .body(BaseResponse.success(dto));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> likeBoard(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[BoardController] [userId={}] like board request, boardId={}", userId, id);

        boardService.likeBoard(id, member);

        log.info("[BoardController] [userId={}] like board success, boardId={}", userId, id);
        return ResponseEntity.ok(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> unlikeBoard(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[BoardController] [userId={}] unlike board request, boardId={}", userId, id);

        boardService.unlikeBoard(id, member);

        log.info("[BoardController] [userId={}] unlike board success, boardId={}", userId, id);
        return ResponseEntity.ok(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> hasUserLikedBoard(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        boolean liked = boardService.hasUserLikedBoard(id, member);
        return ResponseEntity.ok(BaseResponse.success(liked));
    }
}