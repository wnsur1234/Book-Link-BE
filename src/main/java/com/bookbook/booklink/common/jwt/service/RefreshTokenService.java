package com.bookbook.booklink.common.jwt.service;

import com.bookbook.booklink.common.jwt.model.RefreshToken;
import com.bookbook.booklink.common.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(String email, String token) {
        refreshTokenRepository.deleteByEmail(email);
        refreshTokenRepository.save(
            RefreshToken.builder()
                .email(email)
                .token(token)
                .build()
        );
    }
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }

    public Optional<RefreshToken> findByEmail(String email) {
        return refreshTokenRepository.findByEmail(email);
    }
}
