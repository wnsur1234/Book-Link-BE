package com.bookbook.booklink.book_service.controller.docs;

import com.bookbook.booklink.book_service.model.dto.request.LibraryBookRegisterDto;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookSearchReqDto;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookUpdateDto;
import com.bookbook.booklink.book_service.model.dto.response.LibraryBookDetailResDto;
import com.bookbook.booklink.book_service.model.dto.response.LibraryBookListDto;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.dto.PageResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Library Book API", description = "도서관에 등록된 도서 등록/조회/수정 관련 API")
@RequestMapping("/api/library-book")
public interface LibraryBookApiDocs {

    @Operation(
            summary = "도서 등록",
            description = "도서관에 새로운 도서를 등록합니다. " +
                    "하나의 도서관당 동일 도서는 한 번만 등록 가능합니다."
    )
    @ApiErrorResponses({ErrorCode.INVALID_CATEGORY_CODE, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PostMapping
    public ResponseEntity<BaseResponse<UUID>> registerLibraryBook(
            @Valid @RequestBody LibraryBookRegisterDto bookRegisterDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "도서관별 도서 수정",
            description = "도서관에 등록된 도서의 보증금, 보유 권수를 수정합니다."
    )
    @ApiErrorResponses({ErrorCode.BOOK_NOT_FOUND, ErrorCode.NOT_ENOUGH_AVAILABLE_COPIES_TO_REMOVE, ErrorCode.LIBRARY_BOOK_COPIES_MISMATCH,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PatchMapping
    public ResponseEntity<BaseResponse<Void>> updateLibraryBook(
            @Valid @RequestBody LibraryBookUpdateDto updateBookDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "도서관별 도서 삭제",
            description = "도서관에 등록된 도서를 삭제합니다."
    )
    @ApiErrorResponses({ErrorCode.CANNOT_DELETE_BORROWED_BOOK})
    @DeleteMapping("/{libraryBookId}")
    public ResponseEntity<BaseResponse<Void>> deleteLibraryBook(
            @PathVariable @NotNull(message = "삭제할 도서관별 도서의 id는 필수입니다.") UUID libraryBookId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "도서 목록 조회 및 검색",
            description = "위도 및 경도 / 책 제목 / 도서관 id 기반으로 도서 리스트 반환합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR})
    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<LibraryBookListDto>>> getLibraryBookList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @ModelAttribute LibraryBookSearchReqDto request
    );

    @Operation(
            summary = "도서 상세 조회",
            description = "도서관에 등록된 도서의 상세 정보를 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.BOOK_NOT_FOUND})
    @GetMapping("/{libraryBookId}")
    public ResponseEntity<BaseResponse<LibraryBookDetailResDto>> getLibraryBookDetail(
            @NotNull(message = "도서관별 도서 아이디는 필수입니다.") @PathVariable UUID libraryBookId
    );
}
