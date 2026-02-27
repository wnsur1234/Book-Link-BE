package com.bookbook.booklink.board_service.repository;

import com.bookbook.booklink.board_service.model.Board;
import com.bookbook.booklink.board_service.model.BoardLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BoardLikesRepository extends JpaRepository<BoardLikes, UUID> {
    boolean existsByBoardAndUserId(Board board, UUID userId);

    Optional<BoardLikes> findByBoardAndUserId(Board board, UUID userId);
}

