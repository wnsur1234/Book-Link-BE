package com.bookbook.booklink.chat_service.websocket.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("🚀 STOMP CONNECT 요청: user=" + accessor.getUser());
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            System.out.println("📡 SUBSCRIBE 요청: user=" + accessor.getUser());
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            System.out.println("📩 SEND 요청: user=" + accessor.getUser());
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            System.out.println("❎ DISCONNECT 요청: user=" + accessor.getUser());
        }

        return message;
    }
}