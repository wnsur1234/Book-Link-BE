package com.bookbook.booklink.chat_service.single.controller;

import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.single.controller.docs.SingleChatApiDocs;
import com.bookbook.booklink.chat_service.single.model.dto.request.SingleRoomReqDto;
import com.bookbook.booklink.chat_service.single.model.dto.response.SingleRoomResDto;
import com.bookbook.booklink.chat_service.single.service.SingleChatsService;
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
public class SingleChatsController implements SingleChatApiDocs {

    private final SingleChatsService singleChatsService;

    @Override
    public ResponseEntity<BaseResponse<SingleRoomResDto>> createOrGetRoom(
            @RequestBody SingleRoomReqDto reqDto
    ) {
        log.info("[SingleChatsController] createOrGetRoom called. user1Id={}, user2Id={}",
                reqDto.getUser1Id(), reqDto.getUser2Id());

        SingleRoomResDto response = singleChatsService.getOrCreateChatRoom(reqDto);

        log.info("[SingleChatsController] createOrGetRoom called success. user1Id={}, user2Id={}",
                reqDto.getUser1Id(), reqDto.getUser2Id());
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Override
    public ResponseEntity<BaseResponse<MessageResDto>> sendMessage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MessageReqDto dto
    ) {

        log.info("[SingleChatsController] sendMessage called. user.getMember().getId()={}, chatId={}",
                user.getMember().getId(), dto.getChatId());

        MessageResDto response = singleChatsService.saveChatMessages(user.getMember(), dto);

        log.info("[SingleChatsController] sendMessage called success. user.getMember().getId()={}, chatId={}",

                user.getMember().getId(), dto.getChatId());

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Override
    public ResponseEntity<BaseResponse<List<MessageResDto>>> getMessages(
            @PathVariable UUID chatId
    ) {

        log.debug("[SingleChatsController] getMessages called. chatId={}", chatId);

        List<MessageResDto> response = singleChatsService.getChatMessages(chatId);

        log.debug("[SingleChatsController] getMessages called success. chatId={}", chatId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
    