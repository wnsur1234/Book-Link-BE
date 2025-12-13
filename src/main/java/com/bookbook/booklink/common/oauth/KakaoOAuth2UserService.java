package com.bookbook.booklink.common.oauth;

import com.bookbook.booklink.auth_service.code.Provider;
import com.bookbook.booklink.auth_service.code.Role;
import com.bookbook.booklink.auth_service.code.Status;
import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile =
                (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String dummyPassword = passwordEncoder.encode("OAUTH_KAKAO_" + email);

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email(email)
                                .password(dummyPassword)
                                .role(Role.CUSTOMER)
                                .name(nickname)
                                .nickname(nickname)
                                .provider(Provider.KAKAO)
                                .status(Status.ACTIVE)
                                .build()
                ));
        // 이로직 이후에 따로 회원정보를 담아야하나? nickname이라던가 update api 하면 되나?

        return new CustomOAuth2User(member, attributes);
    }
}
