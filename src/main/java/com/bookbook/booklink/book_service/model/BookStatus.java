package com.bookbook.booklink.book_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookStatus {
    AVAILABLE("대여 가능"),
    BORROWED("대여 중"),
    OVERDUE("연체 중"),
    EXTENDED("연장 중");

    private final String description;
}
