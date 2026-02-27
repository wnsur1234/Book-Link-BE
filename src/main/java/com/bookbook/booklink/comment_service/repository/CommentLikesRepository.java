package com.bookbook.booklink.comment_service.repository;

import com.bookbook.booklink.comment_service.model.Comment;
import com.bookbook.booklink.comment_service.model.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, UUID> {

    boolean existsByCommentAndUserId(Comment comment, UUID userId);
    
    Optional<CommentLikes> findByCommentAndUserId(Comment comment, UUID userId);
}
