package com.bookbook.booklink.book_service.model.dto.request;

public enum LibraryBookSortType {
    DISTANCE, // 거리순 (기본값)
    LATEST, // 최신순
    MOST_BORROWED // 가장 많이 빌린 사람 순
}