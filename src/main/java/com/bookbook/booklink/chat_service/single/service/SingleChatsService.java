package com.bookbook.booklink.chat_service.single.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.code.MessageType;
import com.bookbook.booklink.chat_service.chat_mutual.model.ChatMessages;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.chat_mutual.service.ChatMessagesService;
import com.bookbook.booklink.chat_service.single.model.SingleChats;
import com.bookbook.booklink.chat_service.single.model.dto.request.SingleRoomDeleteReqDto;
import com.bookbook.booklink.chat_service.single.model.dto.response.SingleRoomResDto;
import com.bookbook.booklink.chat_service.single.repository.SingleChatsRepository;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

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

        // 내가 다시 채팅을 걸었으면 내 쪽 deleted 플래그 해제
        chat.restoreForUser(me);
        singleChatsRepository.save(chat);

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

    /**
     * 특정 채팅방에서 유저가 ‘나가기’를 수행하는 기능입니다.
     *
     * @param member 나가기를 요청한 사용자 정보
     * @param chatId 대상 채팅방 UUID
     * @throws CustomException CHAT_ROOM_NOT_FOUND — 채팅방이 존재하지 않을 때
     * @throws CustomException CHAT_ROOM_FORBIDDEN — 해당 채팅방의 멤버가 아닐 때
     */
    @Transactional
    public void leaveRoom(Member member, UUID chatId) {

        UUID memberId = member.getId();

        SingleChats room = singleChatsRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.hasMember(memberId)) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FORBIDDEN);
        }

        MessageReqDto systemMessage = MessageReqDto.builder()
                .chatId(chatId)
                .text("상대방이 채팅방을 나갔습니다.")
                .type(MessageType.SYSTEM)
                .build();

        ChatMessages saved = chatMessagesService.saveMessagesEntity(member, systemMessage);

        room.updateLastMessage(saved.getText(), saved.getSentAt());

        // 내 목록에서 숨기기
        room.deleteForUser(memberId);

        singleChatsRepository.save(room);

        // WebSocket 실시간 전송 (상대방이 보고 있는 채팅창)
        MessageResDto resDto = MessageResDto.fromEntity(saved);

        messagingTemplate.convertAndSend("/sub/chat/" + chatId, resDto);

        log.info("[SingleChatsService] leaveRoom. memberId={}, chatId={}", memberId, chatId);
    }

    /**
     * 사용자의 채팅방 목록에서 특정 방들을 삭제(숨김) 처리하는 기능입니다.
     * 실제 메시지나 채팅방은 삭제되지 않으며, 해당 유저의 목록에서만 보이지 않도록 soft-delete 합니다.
     *
     * @param member 삭제를 요청한 사용자
     * @param reqDto chatIds 또는 deleteAll 정보를 포함한 요청 DTO
     * @throws CustomException CHAT_ROOM_NOT_FOUND — 특정 chatId가 존재하지 않을 때
     * @throws CustomException CHAT_ROOM_FORBIDDEN — 삭제하려는 방의 참여자가 아닐 때
     */
    @Transactional
    public void deleteRooms(Member member, SingleRoomDeleteReqDto reqDto) {

        UUID me = member.getId();

        // 전체 삭제
        if (Boolean.TRUE.equals(reqDto.getDeleteAll())) {
            List<SingleChats> rooms = singleChatsRepository.findAllByMember(me);
            for (SingleChats room : rooms) {
                room.deleteForUser(me);
            }
            log.info("[SingleChatsService] deleteRooms ALL. memberId={}, count={}", me, rooms.size());
            return;
        }

        // 단건/다건 삭제
        if (reqDto.getChatIds() == null || reqDto.getChatIds().isEmpty()) {
            return;
        }

        for (UUID chatId : reqDto.getChatIds()) {
            SingleChats room = singleChatsRepository.findById(chatId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

            if (!room.hasMember(me)) {
                throw new CustomException(ErrorCode.CHAT_ROOM_FORBIDDEN);
            }
            room.deleteForUser(me);
        }

        log.info("[SingleChatsService] deleteRooms. memberId={}, size={}",
                me, reqDto.getChatIds().size());
    }
}
    