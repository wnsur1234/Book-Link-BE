package com.bookbook.booklink.book_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "도서관에 있는 책 목록 조회 응답 DTO")
public class LibraryBookListDto {

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final String imageUrl;

    @Schema(description = "도서관별 도서 ID", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    private final UUID id;

    @Schema(description = "책 제목", example = "마흔에 읽는 쇼펜하우어", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String title;

    @Schema(description = "저자명", example = "강용수", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String author;

    @Schema(description = "도서관 이름", example = "서울중앙도서관", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String libraryName;

    @Schema(description = "거리(km)", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Double distance;

    @Schema(description = "책 개수", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private final int copies;

    @Schema(description = "현재 대여 중인 사람 수", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private final int borrowedCount;

    @Schema(description = "보증금 금액", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    private final int deposit;

    @Schema(description = "대여중 여부 (책 개수 == 대여중인 사람 수일 경우 true)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private final boolean rentedOut;

    @Schema(description = "예상 반납 기한 (모든 책이 대여중일 때만 표시)", example = "2025-10-05T00:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final LocalDateTime expectedReturnDate;

    @Schema(description = "내 도서관의 도서인지 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private final boolean isMine;
}
