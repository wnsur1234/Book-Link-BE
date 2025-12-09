package com.bookbook.booklink.chat_service.group.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.code.ChatStatus;
import com.bookbook.booklink.chat_service.chat_mutual.model.ChatMessages;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.chat_mutual.service.ChatMessagesService;
import com.bookbook.booklink.chat_service.group.model.GroupChats;
import com.bookbook.booklink.chat_service.group.model.dto.response.GroupChatRoomResDto;
import com.bookbook.booklink.chat_service.group.repository.GroupChatsRepository;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.community.group_service.model.Group;
import com.bookbook.booklink.community.group_service.repository.GroupMemberRepository;
import com.bookbook.booklink.community.group_service.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupChatsService {

    private final GroupChatsRepository groupChatsRepository;
    private final ChatMessagesService chatMessagesService;
    private final GroupMemberRepository groupMemberRepository;


    /**
     * 모임 생성 시 그룹 채팅방 자동 생성
     */
    @Transactional
    public void createGroupChatRoom(Group savedGroup) {

        // 이미 채팅방이 존재하면 생성하지 않음
        groupChatsRepository.findByGroup(savedGroup)
                .ifPresent(room -> {
                    throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
                });
        GroupChats room = GroupChats.builder()
                .group(savedGroup)
                .lastMessage(null)
                .lastSentAt(null)
                .status(ChatStatus.ACTIVE)
                .build();

        groupChatsRepository.save(room);
    }

    /**
     * 그룹 채팅 메시지 저장
     */
    @Transactional
    public MessageResDto saveGroupChatMessage(Member member, MessageReqDto dto) {
        UUID chatId = dto.getChatId();
        GroupChats room = groupChatsRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 멤버 검증
        boolean isMember = groupMemberRepository.existsByGroupAndMember(room.getGroup(), member);
        if (!isMember) {
            throw new CustomException(ErrorCode.NOT_GROUP_MEMBER);
        }

        ChatMessages saved = chatMessagesService.saveGroupMessagesEntity(member, dto);

        room.updateLastMessage(saved.getText(), saved.getSentAt());
        groupChatsRepository.save(room);

        log.info("[GroupChatsService] message saved. groupChatId={}, sender={}", chatId, member.getId());

        return MessageResDto.fromEntity(saved);
    }

    /**
     * 그룹 채팅 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<MessageResDto> getGroupMessages(UUID chatId, Member me) {
        GroupChats room = groupChatsRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isMember = groupMemberRepository.existsByGroupAndMember(room.getGroup(), me);
        if (!isMember) {
            throw new CustomException(ErrorCode.NOT_GROUP_MEMBER);
        }

        return chatMessagesService.findSentMessages(chatId);
    }

    /**
     * 내가 속한 모든 그룹 채팅 목록 조회
     */
    @Transactional(readOnly = true)
    public List<GroupChatRoomResDto> getMyGroupChatRooms(Member me) {
        List<GroupChats> rooms = groupChatsRepository.findAllByMember(me);

        return rooms.stream()
                .map(GroupChatRoomResDto::from)
                .toList();
    }
}
