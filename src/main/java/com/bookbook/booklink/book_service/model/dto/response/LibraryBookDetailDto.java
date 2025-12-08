package com.bookbook.booklink.book_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "도서 상세 조회를 위한 도서관별 도서 응답 DTO")
public class LibraryBookDetailDto {
    @Schema(description = "도서 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final UUID id;

    @Schema(description = "대여 상태", example = "AVAILABLE(대여 가능), RESERVABLE(예약 가능), BORROWED(대여 중), RESERVED(예약 중)", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 64)
    private final LibraryBookStatus status; // todo : 대여 상태 등으로 변경

    @Schema(description = "책 개수", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer copies;

    @Schema(description = "보증금 금액", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer deposit;

    @Schema(description = "현재 대여 중인 사람 수", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer borrowedCount;

    @Schema(description = "미리보기 이미지", example = "[url 링크]", requiredMode = Schema.RequiredMode.REQUIRED)
    private final List<String> previewImages;

    @Schema(description = "예상 반납 기한", example = "2025-10-05T00:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final LocalDate expectedReturnDate;

    @Schema(description = "대여 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    final UUID borrowId;

    @Schema(description = "대여 상태", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final String borrowedStatus; // todo : 추후 enum 변경

    @Schema(description = "예약 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final UUID reservedId;
}
