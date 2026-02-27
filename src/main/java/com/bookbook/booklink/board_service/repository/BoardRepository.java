package com.bookbook.booklink.board_service.repository;

import com.bookbook.booklink.board_service.model.Board;
import com.bookbook.booklink.board_service.model.BoardCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

    @Query("SELECT b FROM Board b " +
            "WHERE b.deletedAt IS NULL " +
            "AND (:title IS NULL OR b.title LIKE %:title%) " +
            "AND (:category IS NULL OR b.category = :category) ")
    List<Board> findByTitleAndCategory(@Param("title") String title,
                                       @Param("category") BoardCategory category,
                                       Sort sort);
}
    