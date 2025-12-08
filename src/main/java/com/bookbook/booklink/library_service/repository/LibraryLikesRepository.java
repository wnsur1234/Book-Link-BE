package com.bookbook.booklink.library_service.repository;

import com.bookbook.booklink.library_service.model.Library;
import com.bookbook.booklink.library_service.model.LibraryLikes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LibraryLikesRepository extends JpaRepository<LibraryLikes, UUID> {

    boolean existsByLibraryAndUserId(Library library, UUID userId);

    Optional<LibraryLikes> findByLibraryAndUserId(Library library, UUID userId);

    Page<LibraryLikes> findAllByUserId(UUID userId, Pageable pageable);
}
    