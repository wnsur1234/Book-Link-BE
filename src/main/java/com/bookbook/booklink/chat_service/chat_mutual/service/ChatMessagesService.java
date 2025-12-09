package com.bookbook.booklink.chat_service.chat_mutual.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.model.ChatMessages;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.chat_mutual.repository.ChatMessagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessagesService {

    private final ChatMessagesRepository chatMessagesRepository;

    /**
     * 특정 채팅방에 속한 모든 메시지를 전송 시간 순으로 조회합니다.
     *
     * @param chatId 채팅방 UUID
     * @return 메시지 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MessageResDto> findSentMessages(UUID chatId) {
        return chatMessagesRepository.findAllByChatId(chatId)
                .stream()
                .map(MessageResDto::fromEntity)
                .toList();
    }
    @Transactional
    public ChatMessages saveSingleMessagesEntity(Member member, MessageReqDto dto) {
        ChatMessages chatMessages = ChatMessages.saveSingleRoomMessage(member,dto);
        return chatMessagesRepository.save(chatMessages);
    }

    @Transactional
    public ChatMessages saveGroupMessagesEntity(Member member, MessageReqDto dto) {
        ChatMessages chatMessages = ChatMessages.saveGroupMessage(member,dto);
        return chatMessagesRepository.save(chatMessages);
    }
}
