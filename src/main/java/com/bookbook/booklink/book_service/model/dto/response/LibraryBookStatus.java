package com.bookbook.booklink.book_service.model.dto.response;

public enum LibraryBookStatus {
    AVAILABLE,       // 대여 가능
    RESERVABLE,      // 예약 가능
    BORROWED,        // 대여 중
    RESERVED;        // 예약 중
}
