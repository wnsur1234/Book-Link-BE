package com.bookbook.booklink.borrow_service.repository;

import com.bookbook.booklink.borrow_service.model.Borrow;
import com.bookbook.booklink.borrow_service.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, UUID> {

    List<Borrow> findAllByStatusInAndDueAtBefore(List<BorrowStatus> borrowed, LocalDateTime now);

    List<Borrow> findAllByStatusAndBorrowedAtBefore(BorrowStatus borrowStatus, LocalDateTime threeDaysAgo);

    @Query("""
            select b from Borrow b
            join fetch b.libraryBookCopy lbc
            join fetch lbc.libraryBook lb
            where b.id = :borrowId
            """)
    Optional<Borrow> findByIdWithFetchJoin(UUID borrowId);
}