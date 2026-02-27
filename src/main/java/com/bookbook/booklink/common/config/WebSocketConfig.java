package com.bookbook.booklink.common.config;

import com.bookbook.booklink.chat_service.websocket.handler.StompHandler;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomPrincipalHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomPrincipalHandshakeHandler customPrincipalHandshakeHandler;
    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 prefix (클라이언트 → 서버 수신)
        registry.enableSimpleBroker("/sub");
        // 발행 prefix (클라이언트 → 서버 발행)
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트 (SockJS fallback 포함)
        registry.addEndpoint("/ws/chat")
                .setHandshakeHandler(customPrincipalHandshakeHandler)
                .setAllowedOriginPatterns("*");

    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // ✅ 토큰 검증 추가
    }
}