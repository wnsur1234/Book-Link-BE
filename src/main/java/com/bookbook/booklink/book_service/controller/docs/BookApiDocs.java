package com.bookbook.booklink.book_service.controller.docs;

import com.bookbook.booklink.book_service.model.dto.request.BookRegisterDto;
import com.bookbook.booklink.book_service.model.dto.response.BookResponseDto;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.jwt.CustomUserDetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Book API", description = "도서 등록/조회/수정 관련 API")
@RequestMapping("/api/book")
public interface BookApiDocs {

    @Operation(
            summary = "도서 검색",
            description = "도서를 검색합니다. " +
                    "기존 DB에 없을 시 국립중앙도서관 api를 이용해 카테고리 제외한 정보를 반환합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.API_FALLBACK_FAIL, ErrorCode.INVALID_ISBN_CODE,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping("/{isbn}")
    public ResponseEntity<BaseResponse<BookResponseDto>> getBook(
            @PathVariable @NotNull(message = "조회할 도서의 ISBN 코드는 필수입니다.") String isbn,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );

    @Operation(
            summary = "도서 등록",
            description = "기존 DB에 없던 도서를 등록합니다."
    )
    @ApiErrorResponses({ErrorCode.DATABASE_ERROR, ErrorCode.DUPLICATE_BOOK,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PostMapping
    public ResponseEntity<BaseResponse<UUID>> registerBook(
            @Valid @RequestBody BookRegisterDto bookRegisterDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestHeader("Trace-Id") String traceId
    );
}
