package com.bookbook.booklink.review_service.repository;

import com.bookbook.booklink.review_service.model.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewSummaryRepository extends JpaRepository<ReviewSummary, UUID> {
}
