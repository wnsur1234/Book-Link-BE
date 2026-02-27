package com.bookbook.booklink.chat_service.group.model.dto.response;

import com.bookbook.booklink.chat_service.chat_mutual.code.ChatStatus;
import com.bookbook.booklink.chat_service.group.model.GroupChats;
import com.bookbook.booklink.community.group_service.model.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatRoomResDto {
    private UUID chatId;
    private UUID groupId;
    private String groupName;
    private Integer participantCount;
    private String lastMessage;
    private LocalDateTime lastSentAt;
    private ChatStatus status;

    public static GroupChatRoomResDto from(GroupChats entity) {
        Group group = entity.getGroup();

        return GroupChatRoomResDto.builder()
                .chatId(entity.getId())
                .groupId(group.getId())
                .groupName(group.getName())
                .participantCount(group.getParticipantCount())
                .lastMessage(entity.getLastMessage())
                .lastSentAt(entity.getLastSentAt())
                .build();
    }
}
