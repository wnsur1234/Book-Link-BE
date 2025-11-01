package com.bookbook.booklink.book_service.controller;

import com.bookbook.booklink.book_service.controller.docs.BookApiDocs;
import com.bookbook.booklink.book_service.model.dto.request.BookRegisterDto;
import com.bookbook.booklink.book_service.model.dto.response.BookResponseDto;
import com.bookbook.booklink.book_service.service.BookService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class BookController implements BookApiDocs {
    private final BookService bookService;

    @Override
    public ResponseEntity<BaseResponse<BookResponseDto>> getBook(
            @PathVariable @NotNull(message = "조회할 도서의 ISBN 코드는 필수입니다.") String isbn,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BookController] [traceId = {}, userId = {}] find book request received, isbn={}",
                traceId, userId, isbn);

        BookResponseDto book = bookService.getBook(isbn, traceId, userId);

        log.info("[BookController] [traceId = {}, userId = {}] find book request success, book={}",
                traceId, userId, book);

        return ResponseEntity.ok()
                .body(BaseResponse.success(book));
    }

    @Override
    public ResponseEntity<BaseResponse<UUID>> registerBook(
            @Valid @RequestBody BookRegisterDto bookRegisterDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[BookController] [traceId = {}, userId = {}] register book request received, isbn={}",
                traceId, userId, bookRegisterDto.getIsbn());


        UUID savedBookId = bookService.saveBook(bookRegisterDto, traceId, userId);

        log.info("[BookController] [traceId = {}, userId = {}] register book request success, book={}",
                traceId, userId, savedBookId);

        return ResponseEntity.ok(BaseResponse.success(savedBookId));
    }

}
