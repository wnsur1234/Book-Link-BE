package com.bookbook.booklink.library_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.book_service.model.LibraryBook;
import com.bookbook.booklink.book_service.service.LibraryBookService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.dto.PageResponse;
import com.bookbook.booklink.library_service.controller.docs.LibraryApiDocs;
import com.bookbook.booklink.library_service.model.dto.request.LibraryRegDto;
import com.bookbook.booklink.library_service.model.dto.request.LibraryUpdateDto;
import com.bookbook.booklink.library_service.model.dto.response.LibraryDetailDto;
import com.bookbook.booklink.library_service.service.LibraryService;
import com.bookbook.booklink.review_service.model.dto.response.ReviewListDto;
import com.bookbook.booklink.review_service.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class LibraryController implements LibraryApiDocs {
    private final LibraryService libraryService;
    private final ReviewService reviewService;
    private final LibraryBookService libraryBookService;

    @Override
    public ResponseEntity<BaseResponse<UUID>> registerLibrary(
            @Valid @RequestBody LibraryRegDto libraryRegDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();

        log.info("[LibraryController] [traceId = {}, userId = {}] register library request received, name={}",
                traceId, userId, libraryRegDto.getName());

        UUID savedLibraryId = libraryService.registerLibrary(libraryRegDto, traceId, member);

        log.info("[LibraryController] [traceId = {}, userId = {}] register library response success, libraryId={}",
                traceId, userId, savedLibraryId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(savedLibraryId));
    }

    @Override
    public ResponseEntity<BaseResponse<UUID>> updateLibrary(
            @Valid @RequestBody LibraryUpdateDto libraryUpdateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[LibraryController] [traceId = {}, userId = {}] update library request received, libraryId={}",
                traceId, userId, libraryUpdateDto.getLibraryId());

        UUID updatedLibraryId = libraryService.updateLibrary(libraryUpdateDto, traceId, userId);

        log.info("[LibraryController] [traceId = {}, userId = {}] update library response success, libraryId={}",
                traceId, userId, updatedLibraryId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(updatedLibraryId));
    }


    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteLibrary(
            @PathVariable @NotNull(message = "수정할 도서관의 ID는 필수입니다.") UUID id,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[LibraryController] [traceId = {}, userId = {}] delete library request received, libraryId={}",
                traceId, userId, id);

        libraryService.deleteLibrary(id, traceId, userId);

        log.info("[LibraryController] [traceId = {}, userId = {}] delete library response success, libraryId={}",
                traceId, userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<LibraryDetailDto>> getMyLibrary(
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(libraryService.getMyLibrary(member)));
    }

    @Override
    public ResponseEntity<BaseResponse<LibraryDetailDto>> getLibrary(
            @AuthenticationPrincipal(expression = "member") Member member,
            @PathVariable @NotNull(message = "조회할 도서관의 ID는 필수입니다.") UUID id
    ) {
        List<LibraryBook> top5List = libraryBookService.findTop5Books(id);
        List<ReviewListDto> top5Review = reviewService.getTop5LibraryReview(id);
        LibraryDetailDto libraryDetailDto = libraryService.getLibrary(member.getId(), id, top5List, top5Review);

        return ResponseEntity.ok()
                .body(BaseResponse.success(libraryDetailDto));
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponse<LibraryDetailDto>>> getLibraries(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        PageResponse<LibraryDetailDto> result = libraryService.getLibraries(member.getId(), lat, lng, name, pageable);

        return ResponseEntity.ok()
                .body(BaseResponse.success(result));
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponse<LibraryDetailDto>>> getLikedLibraries(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(libraryService.getLikedLibraries(member, pageable)));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> likeLibrary(
            @PathVariable UUID libraryId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        libraryService.likeLibrary(libraryId, member);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> unLikeLibrary(
            @PathVariable UUID libraryId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        libraryService.unlikeLibrary(libraryId, member);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }
}
