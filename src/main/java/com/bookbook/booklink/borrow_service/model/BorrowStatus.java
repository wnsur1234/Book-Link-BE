package com.bookbook.booklink.borrow_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BorrowStatus {
    REQUESTED("대여 대기"),
    BORROWED("대여 중"),
    SUSPENDED("대여 중단"),
    EXTENDED("연장 중"),
    OVERDUE("연체 중"),
    RETURNED("반납 완료")
    ;

    private final String description;
}
