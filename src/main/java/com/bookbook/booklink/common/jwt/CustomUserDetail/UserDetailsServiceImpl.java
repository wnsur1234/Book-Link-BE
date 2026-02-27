package com.bookbook.booklink.common.jwt.CustomUserDetail;


import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    /*
    * Spring Security는 기본적으로 UserDetailsService 인터페이스를 통해 사용자 정보를 가져옵니다.
    * 이를 구현한 CustomUserDetailsService는 데이터베이스에서 사용자 정보를 조회하고 인증 처리를 담당합니다.
    * */
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByEmail(username)
                            .orElseThrow(() -> new UsernameNotFoundException(username));
        return new CustomUserDetails(member);
    }
    
    public List<SimpleGrantedAuthority> findUserAuthorities(String username){
        Member member = memberRepository.findByEmail(username)
                            .orElseThrow(() -> new UsernameNotFoundException(username));
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
        return authorities;
    }
}
