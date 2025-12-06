package com.bookbook.booklink.chat_service.group.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.group.model.dto.response.GroupChatRoomResDto;
import com.bookbook.booklink.community.group_service.model.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupChatsService {

    public void createGroupChatRoom(Group savedGroup) {
    }


    public MessageResDto saveGroupChatMessage(Member member, MessageReqDto dto) {
        return null;
    }

    public List<MessageResDto> getGroupMessages(UUID chatId, Member me) {
        return null;
    }

    public List<GroupChatRoomResDto> getMyGroupChatRooms(Member me) {
        return null;
    }
}
