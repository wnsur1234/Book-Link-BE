package com.bookbook.booklink.borrow_service.controller;

import com.bookbook.booklink.borrow_service.controller.docs.BorrowApiDocs;
import com.bookbook.booklink.borrow_service.model.dto.request.BorrowRequestDto;
import com.bookbook.booklink.borrow_service.service.BorrowService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BorrowController implements BorrowApiDocs {
    private final BorrowService borrowService;

    @Override
    public ResponseEntity<BaseResponse<UUID>> borrowBook(
            @Valid @RequestBody BorrowRequestDto borrowRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId,
            @RequestParam UUID chatId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] borrow book request received, borrowRequestDto={}",
                traceId, userId, borrowRequestDto);

        UUID borrowId = borrowService.borrowBook(customUserDetails.getMember(), traceId, borrowRequestDto,chatId);

        log.info("[BorrowController] [traceId = {}, userId = {}] borrow book request success, borrowId={}",
                traceId, userId, borrowId);
        return ResponseEntity.ok(BaseResponse.success(borrowId));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> requestBorrowConfirmation(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] borrow confirm request received, borrowId={}",
                traceId, userId, borrowId);

        // todo 대여 확정을 요청하는 채팅 전송

        log.info("[BorrowController] [traceId = {}, userId = {}] borrow confirm request success, borrowId={}",
                traceId, userId, borrowId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> acceptBorrowConfirmation(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] accept borrow confirm accept received, borrowId={}",
                traceId, userId, borrowId);

        borrowService.acceptBorrowConfirm(userId, traceId, borrowId);

        log.info("[BorrowController] [traceId = {}, userId = {}] accept borrow confirm request success, borrowId={}",
                traceId, userId, borrowId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> suspendBorrow(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] suspend borrow received, borrowId={}",
                traceId, userId, borrowId);

        borrowService.suspendBorrow(userId, traceId, borrowId);

        log.info("[BorrowController] [traceId = {}, userId = {}] suspend borrow success, borrowId={}",
                traceId, userId, borrowId);
        return ResponseEntity.ok(BaseResponse.success(null));

    }

    @Override
    public ResponseEntity<BaseResponse<Void>> requestReturnBookConfirmation(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] return book accept request received, borrowId={}",
                traceId, userId, borrowId);

        // todo 책 반납 확정 요청

        log.info("[BorrowController] [traceId = {}, userId = {}] return book accept request success, borrowId={}",
                traceId, userId, borrowId);

        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> acceptReturnBookConfirmation(
            @PathVariable UUID borrowId,
            @RequestParam String imageUrl,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] return book confirm accept received, borrowId={}",
                traceId, userId, borrowId);

        borrowService.acceptReturnBookConfirm(borrowId, imageUrl, userId, traceId);

        log.info("[BorrowController] [traceId = {}, userId = {}] return book confirm accept success, borrowId={}",
                traceId, userId, borrowId);

        return ResponseEntity.ok(BaseResponse.success(null));

    }

    @Override
    public ResponseEntity<BaseResponse<Void>> requestBorrowExtend(
            @PathVariable UUID borrowId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] borrow extend request received, borrowId={}",
                traceId, userId, borrowId);

        // todo 대여 연장을 요청하는 채팅 전송

        log.info("[BorrowController] [traceId = {}, userId = {}] borrow extend request success, borrowId={}",
                traceId, userId, borrowId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }


    @Override
    public ResponseEntity<BaseResponse<Void>> acceptBorrowExtend(
            @PathVariable UUID borrowId,
            @NotNull(message = "연장 일자는 필수입니다.") @Future(message = "현재보다 미래여야 합니다.") @RequestParam LocalDate returnDate,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BorrowController] [traceId = {}, userId = {}] accept borrow extend request received, borrowId={}",
                traceId, userId, borrowId);

        borrowService.acceptBorrowExtend(userId, traceId, borrowId, returnDate);

        log.info("[BorrowController] [traceId = {}, userId = {}] accept borrow extend request success, borrowId={}",
                traceId, userId, borrowId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }
}
    