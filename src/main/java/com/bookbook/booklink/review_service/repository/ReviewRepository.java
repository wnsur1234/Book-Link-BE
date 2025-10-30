package com.bookbook.booklink.review_service.repository;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.review_service.model.Review;
import com.bookbook.booklink.review_service.model.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByTargetId(UUID targetId);

    @Query("""
            select r from Review r where r.reviewer = :reviewer and r.targetType = :targetType
            """
    )
    List<Review> findAllByReviewerAndTargetType(Member reviewer, TargetType targetType);

    Optional<Review> findByReviewerAndId(Member reviewer, UUID id);
}
    