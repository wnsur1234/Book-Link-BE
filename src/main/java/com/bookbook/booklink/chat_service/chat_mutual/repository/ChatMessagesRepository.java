package com.bookbook.booklink.chat_service.chat_mutual.repository;

import com.bookbook.booklink.chat_service.chat_mutual.model.ChatMessages;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessagesRepository extends JpaRepository<ChatMessages, UUID> {
    // 특정 채팅방의 메시지를 시간순으로 불러오기
    @EntityGraph(attributePaths = {"sender"})
    List<ChatMessages> findAllByChatId(UUID chatId);

}
