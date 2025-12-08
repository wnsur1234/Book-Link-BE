package com.bookbook.booklink.common.jwt.repository;

import com.bookbook.booklink.common.jwt.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByEmail(String email);
    void deleteByEmail(String email);
}
