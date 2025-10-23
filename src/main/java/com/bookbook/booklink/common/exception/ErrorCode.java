package com.bookbook.booklink.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "처리되지 않은 서버 오류입니다.")
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR_500", "처리되지 않은 서버 오류입니다."),

    @Schema(description = "접근권한이 없습니다.")
    METHOD_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "METHOD_UNAUTHORIZED_401", "접근권한이 없습니다."),

    @Schema(description = "올바른 값이 아닙니다.")
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED_400", "올바른 값이 아닙니다."),

    @Schema(description = "데이터베이스 처리 중 오류가 발생했습니다.")
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR_500", "데이터베이스 처리 중 오류가 발생했습니다."),

    @Schema(description = "데이터 제약 조건을 위반했습니다.")
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "DATA_INTEGRITY_VIOLATION_400", "데이터 제약 조건을 위반했습니다."),

    @Schema(description = "비밀번호는 대/소문자·숫자·특수문자 각 1자 이상 포함, 공백 불가입니다.")
    PASSWORD_POLICY_INVALID(HttpStatus.BAD_REQUEST, "PASSWORD_POLICY_INVALID_400", "비밀번호는 대/소문자·숫자·특수문자 각 1자 이상 포함, 공백 불가"),

    @Schema(description = "이미 등록된 이메일입니다.")
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS_409", "이미 등록된 이메일입니다."),

    @Schema(description = "사용자를 찾을 수 없습니다.")
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND_404", "사용자를 찾을 수 없습니다.."),

    /*
     * 예외처리 예시
     */
    @Schema(description = "로그인할 수 없습니다.")
    USER_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "USER_LOGIN_FAILED_400", "로그인할 수 없습니다."),

    @Schema(description = "이미 처리중인 요청입니다.")
    DUPLICATE_REQUEST(HttpStatus.BAD_REQUEST, "DUPLICATE_REQUEST_400", "이미 처리중인 요청입니다."),

    /*
     * JWT
     */
    @Schema(description = "JWT 토큰이 유효하지 않습니다.")
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN_401", "JWT 토큰이 유효하지 않습니다."),

    @Schema(description = "JWT 토큰이 만료되었습니다.")
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN_401", "JWT 토큰이 만료되었습니다."),

    @Schema(description = "RefreshToken이 유효하지 않습니다.")
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN_401", "RefreshToken이 유효하지 않습니다."),

    @Schema(description = "RefreshToken이 만료되었습니다.")
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_REFRESH_TOKEN_401", "RefreshToken이 만료되었습니다."),

    /*
     * Chatting
     */
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM_NOT_FOUND_400", "채팅방이 존재하지 않습니다."),
    CHAT_ROOM_FORBIDDEN(HttpStatus.FORBIDDEN, "CHAT_ROOM_FORBIDDEN_400", "채팅방 참여자가 아닙니다."),
    CHAT_ROOM_CREATE_CONFLICT(HttpStatus.CONFLICT, "CHAT_ROOM_CREATE_CONFLICT_400", "동일한 채팅방이 이미 존재합니다."),
    MESSAGE_SENDER_MISMATCH(HttpStatus.BAD_REQUEST, "MESSAGE_SENDER_MISMATCH_400", "보내는 사용자 정보가 유효하지 않습니다."),
    /*
     * Library
     */
    @Schema(description = "해당 ID의 도서관이 존재하지 않습니다.")
    LIBRARY_NOT_FOUND(HttpStatus.BAD_REQUEST, "LIBRARY_NOT_FOUND_400", "해당 ID의 도서관이 존재하지 않습니다."),

    /*
     * Book
     */
    @Schema(description = "유효하지 않은 카테고리 코드입니다.")
    INVALID_CATEGORY_CODE(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY_CODE_400", "유효하지 않은 카테고리 코드입니다."),

    @Schema(description = "존재하지 않는 도서입니다.")
    BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOK_NOT_FOUND_400", "존재하지 않는 도서입니다."),

    @Schema(description = "존재하지 않는 도서관별 도서입니다.")
    LIBRARY_BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "LIBRARY_BOOK_NOT_FOUND_400", "존재하지 않는 도서관별 도서입니다."),

    @Schema(description = "공공도서관 api 호출에 실패했습니다.")
    API_FALLBACK_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "API_FALLBACK_FAIL_500", "공공도서관 api 호출에 실패했습니다."),

    @Schema(description = "유효하지 않은 ISBN 코드입니다.")
    INVALID_ISBN_CODE(HttpStatus.BAD_REQUEST, "INVALID_ISBN_CODE_400", "유효하지 않은 ISBN 코드입니다."),

    @Schema(description = "이미 존재하는 도서입니다.")
    DUPLICATE_BOOK(HttpStatus.BAD_REQUEST, "DUPLICATE_BOOK_400", "이미 존재하는 도서입니다."),

    @Schema(description = "보유 권수와 도서 인스턴스 개수가 일치하지 않습니다.")
    LIBRARY_BOOK_COPIES_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "BOOK_INTERNAL_SERVER_ERROR_500", "보유 권수와 도서 인스턴스 개수가 일치하지 않습니다."),

    @Schema(description = "삭제할 도서 개수가 충분하지 않습니다.")
    NOT_ENOUGH_AVAILABLE_COPIES_TO_REMOVE(HttpStatus.BAD_REQUEST, "BOOK_BAD_REQUEST_400", "삭제할 도서 개수가 충분하지 않습니다."),
    @Schema(description = "반납할 수 없는 상태입니다.")
    ILLEGAL_BOOK_STATE(HttpStatus.BAD_REQUEST, "ILLEGAL_BOOK_STATE_400", "반납할 수 없는 상태입니다."),

    @Schema(description = "대여중인 도서가 있을 때는 삭제할 수 없습니다.")
    CANNOT_DELETE_BORROWED_BOOK(HttpStatus.BAD_REQUEST, "BOOK_BAD_REQUEST_400", "대여중인 도서가 있을 때는 삭제할 수 없습니다."),

    @Schema(description = "책이 대여 가능한 상태가 아닙니다.")
    N0T_AVAILABLE_COPY(HttpStatus.INTERNAL_SERVER_ERROR, "COPY_INTERNAL_SERVER_ERROR_500", "책이 대여가능한 상태가 아닙니다."),

    @Schema(description = "연장 일자가 기존 반납일자보다 빠를 수 없습니다.")
    ILLEGAL_EXTEND_DATE(HttpStatus.BAD_REQUEST, "BOOK_BAD_REQUEST_400", "연장 일자가 기존 반납일자보다 빠를 수 없습니다."),

    /*
     * Borrow
     */
    @Schema(description = "존재하지 않는 대여 기록입니다.")
    BORROW_NOT_FOUND(HttpStatus.BAD_REQUEST, "BORROW_NOT_FOUND_400", "존재하지 않는 대여 기록입니다."),

    @Schema(description = "해당 대여 기록에 접근할 수 없는 유저입니다.")
    BORROW_FORBIDDEN(HttpStatus.FORBIDDEN, "BORROW_FORBIDDEN_400", "해당 대여 기록에 접근할 수 없는 유저입니다."),

    @Schema(description = "수행 불가능한 대여 상태입니다.")
    INVALID_BORROW_STATUS(HttpStatus.BAD_REQUEST, "BORROW_INVALID_STATUS_400", "수행 불가능한 대여 상태입니다."),

    /*
     * Review
     */
    @Schema(description = "존재하지 않는 리뷰입니다.")
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW_NOT_FOUND_400", "존재하지 않는 리뷰입니다."),


    /*
     * Notification
     */
    @Schema(description = "존재하지 않는 리뷰입니다.")
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "NOTIFICATION_NOT_FOUND_400", "존재하지 않는 알림입니다."),

    /*
     * Point
     */
    POINT_NOT_FOUND(HttpStatus.BAD_REQUEST, "POINT_NOT_FOUND_400", "포인트가 없습니다."),
    POINT_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "POINT_NOT_ENOUGH_400", "포인트가 부족합니다."),


    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "PAYMENT_NOT_COMPLETED_400", "결제가 진행중입니다!"),
    PAYMENT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PAYMENT_ALREADY_EXISTS_400", "이미 결제요청된 건입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT_NOT_FOUND_400", "잘못된 결제 요청입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_AMOUNT_MISMATCH_400", "잘못된 결제 요청입니다."),
    INVALID_API_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "INVALID_API_TOKEN_500", "포트원 서버의 토큰이 유효하지 않습니다."),
    PAYMENT_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT_CANCEL_FAILED_400", "결제 취소 요청이 실패했습니다."),
    JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON_PARSING_ERROR_500", "JSON 파싱이 실패했습니다."),


    BOARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOARD_NOT_FOUND_400", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND_400", "댓글을 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PARENT_COMMENT_NOT_FOUND_400", "상위 댓글을 찾을 수 없습니다."),
    TOO_MANY_PARENT(HttpStatus.BAD_REQUEST, "TOO_MANY_PARENT_400", "상위 댓글은 하나여야합니다."),
    BOARD_ALREADY_LIKES(HttpStatus.BAD_REQUEST, "BOARD_ALREADY_LIKES_400", "이미 좋아요를 누른 게시글입니다."),
    BOARD_NOT_LIKED(HttpStatus.BAD_REQUEST, "BOARD_NOT_LIKED_400", "좋아요를 누르지 않은 게시글입니다."),
    COMMENT_ALREADY_LIKES(HttpStatus.BAD_REQUEST, "COMMENT_ALREADY_LIKES_400", "이미 좋아요를 누른 댓글입니다."),
    COMMENT_NOT_LIKED(HttpStatus.BAD_REQUEST, "COMMENT_NOT_LIKED_400", "좋아요를 누르지 않은 댓글입니다."),
    COMMENT_DELETED(HttpStatus.BAD_REQUEST, "COMMENT_DELETED_400", "삭제된 댓글입니다."),
    BOARD_DELETED(HttpStatus.BAD_REQUEST, "BOARD_DELETED_400", "삭제된 게시글 입니다"),


    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "GROUP_NOT_FOUND_400", "모임이 존재하지 않습니다."),
    PARTICIPANT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PARTICIPANT_NOT_FOUND_400", "해당 모임의 참여자가 아닙니다."),
    HOST_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "HOST_CANNOT_LEAVE_400", "모임장은 탈퇴할 수 없습니다."),
    ALREADY_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "ALREADY_GROUP_MEMBER_400", "이미 참여한 멤버입니다."),
    GROUP_IS_FULL(HttpStatus.BAD_REQUEST, "GROUP_IS_FULL_400", "모임의 정원이 초과되었습니다."),
    INVALID_GROUP_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_GROUP_PASSWORD_400", "모임 비밀번호가 틀렸습니다."),
    NOT_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "NOT_GROUP_MEMBER_400", "그룹 멤버가 아닙니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "SCHEDULE_NOT_FOUND_400", "일정이 존재하지 않습니다."),
    NOT_SCHEDULE_PARTICIPANT(HttpStatus.BAD_REQUEST, "NOT_SCHEDULE_MEMBER_400", "일정에 참여하지 않은 사용자입니다."),
    ALREADY_SCHEDULE_PARTICIPANT(HttpStatus.BAD_REQUEST, "ALREADY_SCHEDULE_PARTICIPANT_400", "이미 일정에 참여한 사용자입니다."),
    TARGET_NOT_FOUND(HttpStatus.BAD_REQUEST, "TARGET_NOT_FOUND_400", "리뷰대상이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    @Schema(description = "에러 코드", example = "UNKNOWN_ERROR_500", implementation = ErrorCode.class)
    private final String code;

    @Schema(description = "사용자 친화적 메시지", example = "처리되지 않은 서버 오류입니다.")
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}