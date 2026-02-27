package com.bookbook.booklink.point_service.repository;

import com.bookbook.booklink.point_service.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, UUID> {

    List<PointHistory> findAllByMember_IdOrderByCreatedAt(UUID userId);

}
    