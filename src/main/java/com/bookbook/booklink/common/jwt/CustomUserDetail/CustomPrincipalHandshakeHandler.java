package com.bookbook.booklink.common.jwt.CustomUserDetail;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.repository.MemberRepository;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.util.JWTUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
public class CustomPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    public CustomPrincipalHandshakeHandler(JWTUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
//        // HTTP 헤더에서 Authorization 추출
//        List<String> authValues = request.getHeaders().get("Authorization");
//        if (authValues == null || authValues.isEmpty()) {
//            System.out.println("❌ Authorization header missing");
//            return null;
//        }
//
//        String token = authValues.get(0).replace("Bearer ", "");
//        if (!jwtUtil.validateToken(token)) {
//            System.out.println("❌ Invalid token");
//            return null;
//        }
        // ✅ 1️⃣ URI에서 token 파라미터 추출
        String uri = request.getURI().toString();
        System.out.println("🌐 Handshake URI: " + uri);

        if (!uri.contains("token=")) {
            System.out.println("❌ Token missing in URI");
            return null;
        }

        String token = uri.substring(uri.indexOf("token=") + 6);
        System.out.println("🔑 Extracted token: " + token);

        // ✅ 2️⃣ JWT 검증
        if (!jwtUtil.validateToken(token)) {
            System.out.println("❌ Invalid token");
            return null;
        }

        String email = jwtUtil.getUsername(token);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(member);
        System.out.println("✅ Handshake Principal created: " + userDetails.getUsername());
        return userDetails; // Principal 등록
    }
}