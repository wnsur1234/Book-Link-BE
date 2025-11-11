package com.bookbook.booklink.chat_service.single.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.model.ChatMessages;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.chat_mutual.service.ChatMessagesService;
import com.bookbook.booklink.chat_service.single.model.SingleChats;
import com.bookbook.booklink.chat_service.single.model.dto.request.SingleRoomReqDto;
import com.bookbook.booklink.chat_service.single.model.dto.response.SingleRoomResDto;
import com.bookbook.booklink.chat_service.single.repository.SingleChatsRepository;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SingleChatsService {
    private final SingleChatsRepository singleChatsRepository;
    private final ChatMessagesService chatMessagesService;

    /**
     * 두 사용자의 채팅방을 조회하거나 없으면 새로 생성합니다.
     * <p>
     * - user1-user2, user2-user1 조합을 모두 확인합니다. <br>
     * - 기존 채팅방이 없을 경우 새로 생성 후 저장합니다.
     *
     * @param me 채팅방 생성 요청 DTO (user1Id, user2Id 포함)
     * @return 생성되었거나 조회된 채팅방 응답 DTO
     */
    @Transactional
    public SingleRoomResDto getOrCreateChatRoom(UUID me, UUID chatPartner) {

        if (me == null || chatPartner == null) {
            throw new CustomException(ErrorCode.CHAT_ROOM_INVALID_MEMBER);
        }

        UUID u1 = me.compareTo(chatPartner) <= 0 ? me : chatPartner;
        UUID u2 = me.compareTo(chatPartner) <= 0 ? chatPartner : me;

        SingleChats chat = singleChatsRepository.findByUser1IdAndUser2Id(u1, u2)
                .orElseGet(() -> singleChatsRepository.save(SingleChats.createNormalized(u1, u2)));

        return SingleRoomResDto.fromEntity(chat);
    }

    @Transactional(readOnly = true)
    public List<SingleRoomResDto> getMyRooms(UUID memberId) {
        List<SingleChats> rooms =
                singleChatsRepository.findAllByMemberSorted(memberId);

        return rooms.stream()
                .map(SingleRoomResDto::fromEntity)
                .toList();
    }


    @Transactional
    public MessageResDto saveChatMessages(Member member, MessageReqDto dto) {

        UUID memberId = member.getId();
        UUID chatId = dto.getChatId();

        log.debug("[SingleChatsService] saveChatMessages called. memberId={}, chatId={}", memberId, chatId);

        SingleChats room = singleChatsRepository.findById(dto.getChatId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.hasMember(member.getId())) {throw new CustomException(ErrorCode.CHAT_ROOM_FORBIDDEN);}

        ChatMessages saved = chatMessagesService.saveMessagesEntity(member,dto);


        room.updateLastMessage(saved.getText(), saved.getSentAt());
        singleChatsRepository.save(room);

        log.info("[SingleChatsService] Last message updated. roomId={}, senderId={}",
                room.getId(), memberId);

        return MessageResDto.fromEntity(saved);
    }

    /**
     * 특정 채팅방의 모든 메시지를 조회합니다.
     * <p>
     * - 내부적으로 {@link ChatMessagesService#findSentMessages(UUID)} 호출합니다.
     *
     * @param chatId 채팅방 UUID
     * @return 메시지 응답 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MessageResDto> getChatMessages(UUID chatId){
        return chatMessagesService.findSentMessages(chatId);
    }

}
    