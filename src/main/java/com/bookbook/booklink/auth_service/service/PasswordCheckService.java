package com.bookbook.booklink.auth_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasswordCheckService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean verifyPassword(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return passwordEncoder.matches(password, member.getPassword());
    }
}
