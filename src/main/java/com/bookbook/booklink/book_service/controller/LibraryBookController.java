package com.bookbook.booklink.book_service.controller;

import com.bookbook.booklink.book_service.controller.docs.LibraryBookApiDocs;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookRegisterDto;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookSearchReqDto;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookUpdateDto;
import com.bookbook.booklink.book_service.model.dto.response.LibraryBookDetailResDto;
import com.bookbook.booklink.book_service.model.dto.response.LibraryBookListDto;
import com.bookbook.booklink.book_service.service.LibraryBookService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.dto.PageResponse;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import com.bookbook.booklink.library_service.service.LibraryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class LibraryBookController implements LibraryBookApiDocs {
    private final LibraryBookService bookService;
    private final LibraryService libraryService;
    private final LibraryBookService libraryBookService;

    @Override
    public ResponseEntity<BaseResponse<UUID>> registerLibraryBook(
            @Valid @RequestBody LibraryBookRegisterDto bookRegisterDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[LibraryBookController] [traceId = {}, userId = {}] register book request received, bookId={}",
                traceId, userId, bookRegisterDto.getId());

        UUID savedLibraryBookId = bookService.registerLibraryBook(bookRegisterDto, traceId, userId, libraryService.findByUserId(userId));

        log.info("[LibraryBookController] [traceId = {}, userId = {}] register book request success, libraryBookId={}",
                traceId, userId, savedLibraryBookId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(savedLibraryBookId));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> updateLibraryBook(
            @Valid @RequestBody LibraryBookUpdateDto updateBookDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[LibraryBookController] [traceId = {}, userId = {}] update library book request received, libraryBookId={}",
                traceId, userId, updateBookDto.getId());

        bookService.updateLibraryBook(updateBookDto, traceId, userId);

        log.info("[LibraryBookController] [traceId = {}, userId = {}] update library book request success, libraryBookId={}",
                traceId, userId, updateBookDto.getId());

        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Override
    public ResponseEntity<BaseResponse<Void>> deleteLibraryBook(
            @PathVariable @NotNull(message = "삭제할 도서관별 도서의 id는 필수입니다.") UUID libraryBookId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = customUserDetails.getMember().getId();

        log.info("[LibraryBookController] [traceId = {}, userId = {}] delete library book request received, libraryBookId={}",
                traceId, userId, libraryBookId);

        bookService.deleteLibraryBook(libraryBookId, traceId, userId);

        log.info("[LibraryBookController] [traceId = {}, userId = {}] delete library book request success, libraryBookId={}",
                traceId, userId, libraryBookId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponse<LibraryBookListDto>>> getLibraryBookList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @ModelAttribute LibraryBookSearchReqDto request
    ) {
        UUID userId = customUserDetails.getMember().getId();

        PageResponse<LibraryBookListDto> response = libraryBookService.getLibraryBookList(request, userId);

        return ResponseEntity.ok(
                BaseResponse.success(response)
        );
    }

    @Override
    public ResponseEntity<BaseResponse<LibraryBookDetailResDto>> getLibraryBookDetail(
            @NotNull(message = "도서관별 도서 아이디는 필수입니다.") @PathVariable UUID libraryBookId
    ) {
        LibraryBookDetailResDto response = libraryBookService.getLibraryBookDetail(libraryBookId);

        return ResponseEntity.ok(BaseResponse.success(
                response
        ));
    }
}
    