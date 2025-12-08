package com.bookbook.booklink.chat_service.chat_mutual.code;

/**
 * 메시지의 현재 상태(전송 및 수신 흐름)를 정의합니다.
 * - 프론트엔드에서 "읽음/안읽음" 표시 등을 구현할 때 사용됩니다.
 * - DB의 chat_messages.status 컬럼과 매핑됩니다.
 */
public enum MessageStatus {
    SENT,
    DELIVERED,
    READ,
    FAILED,
    DELETED
}
