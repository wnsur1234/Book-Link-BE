package com.bookbook.booklink.chat_service.group.controller.docs;

import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.group.model.dto.response.GroupChatRoomResDto;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "그룹 채팅 API", description = "독서 모임(Group) 내 그룹 채팅 메시지 관련 API")
@RequestMapping("/api/groupChats")
public interface GroupChatApiDocs {

    @Operation(
            summary = "그룹 채팅 메시지 보내기",
            description = "특정 그룹 채팅방에 메시지를 전송합니다."
    )
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.NOT_GROUP_MEMBER
    })
    @PostMapping("/send")
    ResponseEntity<BaseResponse<MessageResDto>> sendGroupMessage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MessageReqDto dto
    );

    @Operation(
            summary = "그룹 채팅방 메시지 조회",
            description = "특정 그룹 채팅방의 모든 메시지를 시간순으로 조회합니다."
    )
    @ApiErrorResponses({
            ErrorCode.DATABASE_ERROR,
            ErrorCode.SCHEDULE_NOT_FOUND
    })
    @GetMapping("/room/{chatId}/messages")
    ResponseEntity<BaseResponse<List<MessageResDto>>> getGroupMessages(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(
            summary = "내 그룹 채팅방 목록 조회",
            description = "현재 사용자가 참여 중인 그룹(모임)의 채팅방 목록을 조회합니다."
    )
    @ApiErrorResponses({
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/rooms")
    ResponseEntity<BaseResponse<List<GroupChatRoomResDto>>> getMyGroupChatRooms(
            @AuthenticationPrincipal CustomUserDetails user
    );
}