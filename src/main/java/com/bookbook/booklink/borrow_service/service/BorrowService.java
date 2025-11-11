package com.bookbook.booklink.borrow_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.book_service.model.LibraryBook;
import com.bookbook.booklink.book_service.model.LibraryBookCopy;
import com.bookbook.booklink.book_service.service.LibraryBookService;
import com.bookbook.booklink.borrow_service.model.Borrow;
import com.bookbook.booklink.borrow_service.model.BorrowStatus;
import com.bookbook.booklink.borrow_service.model.dto.request.BorrowRequestDto;
import com.bookbook.booklink.borrow_service.repository.BorrowRepository;
import com.bookbook.booklink.chat_service.chat_mutual.code.MessageType;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.request.MessageReqDto;
import com.bookbook.booklink.chat_service.chat_mutual.model.dto.response.MessageResDto;
import com.bookbook.booklink.chat_service.single.service.SingleChatsService;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.point_service.model.TransactionType;
import com.bookbook.booklink.point_service.model.dto.request.PointUseDto;
import com.bookbook.booklink.point_service.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final LibraryBookService libraryBookService;
    private final PointService pointService;
    private final SingleChatsService singleChatsService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public UUID borrowBook(Member member, String traceId, BorrowRequestDto borrowRequestDto,UUID chatId) {
        log.info("[BorrowService] [traceId = {}, userId = {}] borrow book initiate borrowRequestDto={}", traceId, member.getId(), borrowRequestDto);

        UUID libraryBookId = borrowRequestDto.getLibraryBookId();
        LocalDateTime borrowedAt = LocalDateTime.now();
        LocalDateTime dueAt = borrowRequestDto.getExpectedReturnDate();

        LibraryBook libraryBook = libraryBookService.getLibraryBookOrThrow(libraryBookId);
        LibraryBookCopy copy = libraryBookService.getLibraryBookCopy(libraryBookId);

        Borrow borrow = Borrow.createBorrow(copy, member, borrowedAt, dueAt);
        libraryBook.borrowCopy(copy, borrowedAt, dueAt);

        int deposit = copy.getLibraryBook().getDeposit();
        if (deposit > 0) {
            PointUseDto dto = PointUseDto.builder()
                    .amount(deposit)
                    .type(TransactionType.USE)
                    .build();
            pointService.usePoint(dto, UUID.fromString(traceId), member);
        }

        Borrow savedBorrow = borrowRepository.save(borrow);
        UUID borrowId = savedBorrow.getId();

        sendBorrowRequestMessage(chatId, member, savedBorrow);

        log.info("[BorrowService] [traceId = {}, userId = {}] borrow book success borrowId={}", traceId, member.getId(), borrowId);
        return borrowId;
    }

    @Transactional
    public void acceptBorrowConfirm(UUID userId, String traceId, UUID borrowId) {
        log.info("[BorrowService] [traceId = {}, userId = {}] accept borrow confirm initiate borrowId={}", traceId, userId, borrowId);

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new CustomException(ErrorCode.BORROW_NOT_FOUND));

        UUID memberId = borrow.getMember().getId();
        UUID libraryOwnerId = borrow.getLibraryBookCopy().getLibraryBook().getLibrary().getMember().getId();
        if (!userId.equals(memberId) && !userId.equals(libraryOwnerId)) {
            throw new CustomException(ErrorCode.BORROW_FORBIDDEN);
        }

        borrow.setBorrowed();

        log.info("[BorrowService] [traceId = {}, userId = {}] accept borrow confirm success borrowId={}", traceId, userId, borrowId);

    }

    @Transactional
    public void suspendBorrow(UUID userId, String traceId, UUID borrowId) {
        log.info("[BorrowService] [traceId = {}, userId = {}] suspend borrow initiate borrowId={}", traceId, userId, borrowId);

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new CustomException(ErrorCode.BORROW_NOT_FOUND));

        if (!(borrow.getStatus().equals(BorrowStatus.BORROWED)
                || borrow.getStatus().equals(BorrowStatus.EXTENDED)
                || borrow.getStatus().equals(BorrowStatus.OVERDUE))) {
            throw new CustomException(ErrorCode.INVALID_BORROW_STATUS);
        }

        UUID memberId = borrow.getMember().getId();
        UUID libraryOwnerId = borrow.getLibraryBookCopy().getLibraryBook().getLibrary().getMember().getId();
        if (!userId.equals(memberId) && !userId.equals(libraryOwnerId)) {
            throw new CustomException(ErrorCode.BORROW_FORBIDDEN);
        }

        borrow.suspendBorrow();

        log.info("[BorrowService] [traceId = {}, userId = {}] suspend borrow success borrowId={}", traceId, userId, borrowId);

    }

    @Transactional
    public void acceptReturnBookConfirm(UUID borrowId, String imageUrl, UUID userId, String traceId) {
        log.info("[BorrowService] [traceId = {}, userId = {}] return book confirm accept initiate borrowId={}", traceId, userId, borrowId);

        Borrow borrow = borrowRepository.findByIdWithFetchJoin(borrowId)
                .orElseThrow(() -> new CustomException(ErrorCode.BORROW_NOT_FOUND));

        if (!(borrow.getStatus().equals(BorrowStatus.BORROWED)
        || borrow.getStatus().equals(BorrowStatus.EXTENDED)
        || borrow.getStatus().equals(BorrowStatus.OVERDUE))) {
            throw new CustomException(ErrorCode.INVALID_BORROW_STATUS);
        }

        UUID libraryOwnerId = borrow.getLibraryBookCopy().getLibraryBook().getOwnerId();
        if (!userId.equals(libraryOwnerId)) {
            throw new CustomException(ErrorCode.BORROW_FORBIDDEN);
        }

        LibraryBookCopy copy = borrow.getLibraryBookCopy();
        LibraryBook libraryBook = copy.getLibraryBook();

        borrow.returnBook(LocalDateTime.now(), imageUrl);
        libraryBook.returnCopy(copy);

        log.info("[BorrowService] [traceId = {}, userId = {}] return book confirm accept success borrowId={}", traceId, userId, borrowId);
    }

    @Transactional
    public void acceptBorrowExtend(UUID userId, String traceId, UUID borrowId, LocalDate returnDate) {
        log.info("[BorrowService] [traceId = {}, userId = {}] accept book extend initiate borrowId={}", traceId, userId, borrowId);

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new CustomException(ErrorCode.BORROW_NOT_FOUND));

        if (!borrow.getStatus().equals(BorrowStatus.BORROWED)) {
            throw new CustomException(ErrorCode.INVALID_BORROW_STATUS);
        }

        UUID libraryOwnerId = borrow.getLibraryBookCopy().getLibraryBook().getLibrary().getMember().getId();
        if (!userId.equals(libraryOwnerId)) {
            throw new CustomException(ErrorCode.BORROW_FORBIDDEN);
        }

        borrow.extendBook(returnDate.atStartOfDay());

        log.info("[BorrowService] [traceId = {}, userId = {}] accept book extend success borrowId={}", traceId, userId, borrowId);

    }

    private void sendBorrowRequestMessage(UUID chatId, Member sender, Borrow borrow) {

        // ✅ 1. 저장용 DTO 생성 (기존 WebSocket에서 쓰던 형태 재사용)
        MessageReqDto messageReqDto = MessageReqDto.builder()
                .chatId(chatId)
                .content("[대여 요청] " + borrow.getLibraryBookCopy().getLibraryBook().getBook().getTitle())
                .type(MessageType.SYSTEM)  // 시스템 메시지용 타입이 있다면
                .build();

        // ✅ 2. 기존 로직 재사용: DB 저장
        MessageResDto saved = singleChatsService.saveChatMessages(sender, messageReqDto);

        // ✅ 3. WebSocket 구독자에게 전송
        messagingTemplate.convertAndSend("/sub/chat/" + chatId, saved);
    }
}
    