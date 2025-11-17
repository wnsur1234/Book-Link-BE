package com.bookbook.booklink.borrow_service.controller.docs;

import com.bookbook.booklink.borrow_service.model.dto.request.BorrowRequestDto;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Borrow API", description = "도서 대여 관련 API")
@RequestMapping("/api/borrows")
public interface BorrowApiDocs {

    @Operation(
            summary = "도서 한 개 대여",
            description = "도서 한 개를 대여합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.BOOK_NOT_FOUND,
            ErrorCode.USER_NOT_FOUND, ErrorCode.N0T_AVAILABLE_COPY})
    @PostMapping
    public ResponseEntity<BaseResponse<UUID>> borrowBook(
            @Valid @RequestBody BorrowRequestDto borrowRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId,
            @RequestParam UUID chatId
    );

    @Operation(
            summary = "대여 확정 요청",
            description = "대여 확정 요청 채팅을 전송합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR /*todo 에러 코드 추가*/})
    @PostMapping("/{borrowId}/confirm-requests")
    public ResponseEntity<BaseResponse<Void>> requestBorrowConfirmation(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId,
            @RequestParam UUID chatId
    );

    @Operation(
            summary = "대여 확정 수락",
            description = "대여 확정 요청을 수락합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.BORROW_NOT_FOUND, ErrorCode.BORROW_FORBIDDEN})
    @PatchMapping("/{borrowId}/confirm")
    public ResponseEntity<BaseResponse<Void>> acceptBorrowConfirmation(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "대여 중단",
            description = "대여를 중단합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.BORROW_NOT_FOUND, ErrorCode.BORROW_FORBIDDEN})
    @PatchMapping("/{borrowId}/suspend")
    public ResponseEntity<BaseResponse<Void>> suspendBorrow(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "반납 확정 요청",
            description = "반납 확정 요청 채팅을 전송합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR /*todo 에러 코드 추가*/})
    @PostMapping("/{borrowId}/return-requests")
    public ResponseEntity<BaseResponse<Void>> requestReturnBookConfirmation(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "반납 수락",
            description = "도서관 주인이 반납 확정 요청을 수락합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.BORROW_NOT_FOUND, ErrorCode.INVALID_BORROW_STATUS
    , ErrorCode.BORROW_FORBIDDEN})
    @PatchMapping("{borrowId}/return")
    public ResponseEntity<BaseResponse<Void>> acceptReturnBookConfirmation(
            @PathVariable UUID borrowId,
            @NotBlank(message = "반납 인증 사진은 필수입니다.") @RequestParam String imageUrl,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "도서 대여 연장 요청",
            description = "대여 연장이 필요할 시 대여 연장 채팅을 전송합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR /*todo 에러 코드 추가*/})
    @PostMapping("/{borrowId}/extend-requests")
    public ResponseEntity<BaseResponse<Void>> requestBorrowExtend(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );


    @Operation(
            summary = "대여 연장 수락",
            description = "대여 연장을 수락합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.BORROW_NOT_FOUND,
            ErrorCode.INVALID_BORROW_STATUS, ErrorCode.BORROW_FORBIDDEN})
    @PatchMapping("/{borrowId}/extend")
    public ResponseEntity<BaseResponse<Void>> acceptBorrowExtend(
            @PathVariable UUID borrowId,
            @NotNull(message = "연장 일자는 필수입니다.") @Future(message = "현재보다 미래여야 합니다.") @RequestParam LocalDate returnDate,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );
}
