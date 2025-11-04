package com.bookbook.booklink.auth_service.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import com.bookbook.booklink.auth_service.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    boolean existsByNickname(String nickname);
}
    