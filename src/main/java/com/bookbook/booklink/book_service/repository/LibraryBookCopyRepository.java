package com.bookbook.booklink.book_service.repository;

import com.bookbook.booklink.book_service.model.LibraryBookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LibraryBookCopyRepository extends JpaRepository<LibraryBookCopy, UUID> {
}
    