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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SingleChatsController implements SingleChatApiDocs {

    private final SingleChatsService singleChatsService;

    @Override
    public ResponseEntity<BaseResponse<SingleRoomResDto>> createOrGetRoom(
            @RequestBody SingleRoomReqDto reqDto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UUID me = user.getMember().getId();
        UUID chatPartner = reqDto.getChatPartner();

        SingleRoomResDto response =
                singleChatsService.getOrCreateChatRoom(me,chatPartner);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Override
    public ResponseEntity<BaseResponse<List<SingleRoomResDto>>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UUID me = user.getMember().getId();
        List<SingleRoomResDto> rooms = singleChatsService.getMyRooms(me);
        return ResponseEntity.ok(BaseResponse.success(rooms));
    }

    @Override
    public ResponseEntity<BaseResponse<MessageResDto>> sendMessage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MessageReqDto dto
    ) {
        System.out.println(user.getMember().getId());
        MessageResDto response =
                singleChatsService.saveChatMessages(user.getMember(),dto);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Override
    public ResponseEntity<BaseResponse<List<MessageResDto>>> getMessages(
            @PathVariable UUID chatId
    ) {
        List<MessageResDto> response = singleChatsService.getChatMessages(chatId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
    