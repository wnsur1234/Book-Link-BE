package com.bookbook.booklink.chat_service.chat_mutual.code;

/**
 * 채팅방의 상태를 정의합니다.
 * - 방이 유효한지, 차단되었는지, 삭제되었는지 등을 관리합니다.
 * - DB의 one_to_one_chats.status 또는 group_chats.status 컬럼과 매핑됩니다.
 */
public enum ChatStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED,
    DELETED
}
