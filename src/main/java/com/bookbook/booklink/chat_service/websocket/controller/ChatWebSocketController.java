package com.bookbook.booklink.chat_service.websocket.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.group.service.GroupChatsService;
import com.bookbook.booklink.chat_service.single.service.SingleChatsService;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SingleChatsService singleChatsService;
    private final SimpMessagingTemplate messagingTemplate;
    private final GroupChatsService groupChatsService;

    // 클라이언트가 /pub/chat.send 로 메시지 발행
    @MessageMapping("/chat/send")
    public void sendMessage(
            MessageReqDto dto, Principal principal
    ) {

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        Member member = userDetails.getMember();

        log.debug("[ChatWebSocketController] WebSocket send request. memberId={}, chatId={}",
                member.getId(), dto.getChatId());

        MessageResDto saved = singleChatsService.saveChatMessages(member, dto);

        // 구독자에게 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/" + dto.getChatId(), saved);

        log.info("[ChatWebSocketController] WebSocket message sent. memberId={}, chatId={}",
                member.getId(), dto.getChatId());
    }

    @MessageMapping("/group/send")
    public void sendGroupMessage(MessageReqDto dto, Principal principal) {

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        Member member = userDetails.getMember();

        log.debug("[GroupChat] WebSocket send request. memberId={}, chatId={}",
                member.getId(), dto.getChatId());

        // ✅ 여기서 GroupChatsService 사용
        MessageResDto saved = groupChatsService.saveGroupChatMessage(member, dto);

        // ✅ 구독 주소: /sub/group/{chatId}
        messagingTemplate.convertAndSend("/sub/group/" + dto.getChatId(), saved);

        log.info("[GroupChat] WebSocket message sent. memberId={}, chatId={}",
                member.getId(), dto.getChatId());
    }
}