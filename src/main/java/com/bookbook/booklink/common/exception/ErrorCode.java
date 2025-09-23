package com.bookbook.booklink.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 양식
 * <p>
 * 1. 이름: 도메인_상황
 * 2. 내용: http 상태코드, 분류코드(이름_상태코드), 사용자 친화적 메시지
 * 각 도메인 별로 ErrorCode를 분리해주세요.
 */
@Getter
public enum ErrorCode {
    /*
    GlobalExceptionHandler에서 사용
     */
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR_500", "처리되지 않은 서버 오류입니다."),
    METHOD_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "METHOD_UNAUTHORIZED_401", "접근권한이 없습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED_400", "올바른 값이 아닙니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR_500", "데이터베이스 처리 중 오류가 발생했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION_400", "데이터 제약 조건을 위반했습니다."),

    /*
     * 예외처리 예시
     */
    USER_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "USER_LOGIN_FAILED_400", "로그인할 수 없습니다."),
    DUPLICATE_REQUEST(HttpStatus.BAD_REQUEST, "DUPLICATE_REQUEST_400", "이미 처리중인 요청입니다."),

    /*
     * Library
     */
    LIBRARY_NOT_FOUND(HttpStatus.BAD_REQUEST, "LIBRARY_NOT_FOUND_400", "해당 ID의 도서관이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}