package com.bookbook.booklink.chat_service.group.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.group.controller.docs.GroupChatApiDocs;
import com.bookbook.booklink.chat_service.group.model.dto.response.GroupChatRoomResDto;
import com.bookbook.booklink.chat_service.group.service.GroupChatsService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupChatsController implements GroupChatApiDocs {

    private final GroupChatsService groupChatsService;

    @Override
    public ResponseEntity<BaseResponse<MessageResDto>> sendGroupMessage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MessageReqDto dto
    ) {
        Member me = user.getMember();
        UUID chatId = dto.getChatId();

        log.info("[GroupChatsController] sendGroupMessage called. memberId={}, chatId={}",
                me.getId(), chatId);

        MessageResDto res = groupChatsService.saveGroupChatMessage(me, dto);

        log.info("[GroupChatsController] sendGroupMessage success. memberId={}, chatId={}",
                me.getId(), chatId);

        return ResponseEntity.ok(BaseResponse.success(res));
    }

    @Override
    public ResponseEntity<BaseResponse<List<MessageResDto>>> getGroupMessages(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Member me = user.getMember();

        log.debug("[GroupChatsController] getGroupMessages called. memberId={}, chatId={}",
                me.getId(), chatId);

        List<MessageResDto> messages = groupChatsService.getGroupMessages(chatId, me);

        log.debug("[GroupChatsController] getGroupMessages success. memberId={}, chatId={}",
                me.getId(), chatId);

        return ResponseEntity.ok(BaseResponse.success(messages));
    }

    @Override
    public ResponseEntity<BaseResponse<List<GroupChatRoomResDto>>> getMyGroupChatRooms(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Member me = user.getMember();

        log.debug("[GroupChatsController] getMyGroupChatRooms called. memberId={}", me.getId());

        List<GroupChatRoomResDto> rooms = groupChatsService.getMyGroupChatRooms(me);

        log.debug("[GroupChatsController] getMyGroupChatRooms success. memberId={}, size={}",
                me.getId(), rooms.size());

        return ResponseEntity.ok(BaseResponse.success(rooms));
    }
}