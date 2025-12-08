package com.bookbook.booklink.comment_service.repository;

import com.bookbook.booklink.comment_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.board b JOIN FETCH b.member WHERE c.id = :commentId")
    Optional<Comment> findByIdWithBoard(@Param("commentId") UUID commentId);

    List<Comment> findByBoardIdAndParentIsNullAndDeletedAtIsNullOrderByCreatedAtAsc(UUID boardId);
}
    