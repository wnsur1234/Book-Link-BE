package com.bookbook.booklink.chat_service.chat_mutual.code;

/**
 * 메시지의 콘텐츠 종류를 정의합니다.
 * - 채팅 클라이언트(UI)에서 메시지를 어떤 형태로 렌더링할지 구분하는 데 사용됩니다.
 * - DB의 chat_messages.type 컬럼과 매핑됩니다.
 */
public enum MessageType {
    TEXT,
    IMAGE,
    FILE,
    VIDEO,
    AUDIO,
    SYSTEM
}
