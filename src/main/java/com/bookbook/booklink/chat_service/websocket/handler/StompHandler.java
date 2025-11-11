package com.bookbook.booklink.chat_service.websocket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        Principal user = accessor.getUser();
        String userName = (user != null) ? user.getName() : "anonymous";

        if (command == null) {
            return message;
        }

        switch (command) {
            case CONNECT -> log.info("[STOMP] CONNECT: user={}", userName);
            case SUBSCRIBE -> log.debug("[STOMP] SUBSCRIBE: user={}, destination={}",
                    userName, accessor.getDestination());
            case SEND -> log.debug("[STOMP] SEND: user={}, destination={}",
                    userName, accessor.getDestination());
            case DISCONNECT -> log.info("[STOMP] DISCONNECT: user={}", userName);
            default -> {
                // 필요 시 TRACE 수준으로 더 넣을 수 있음
                log.trace("[STOMP] {}: user={}", command, userName);
            }
        }

        return message;
    }
}