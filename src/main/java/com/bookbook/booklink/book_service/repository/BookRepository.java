package com.bookbook.booklink.book_service.repository;

import com.bookbook.booklink.book_service.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByISBN(String isbn);

    Book findByISBN(String isbn);
}
    