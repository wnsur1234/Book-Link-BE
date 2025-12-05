package com.bookbook.booklink.book_service.model.dto.response;

import com.bookbook.booklink.book_service.model.BookCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도서 등록을 위한 도서 조회 응답 DTO")
public class BookResponseDto {
    @Schema(description = "도서 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID id;
    @Schema(description = "도서 이름", example = "마흔에 읽는 쇼펜하우어", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 64)
    private String title;
    @Schema(description = "저자명", example = "강용수", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 16)
    private String author;
    @Schema(description = "출판사", example = "유노북스", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 16)
    private String publisher;
    @Schema(description = "카테고리", example = "GENERALITIES", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BookCategory category;
    @Schema(description = "ISBN 코드", example = "9791192300818", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ISBN;
    @Schema(description = "도서 정가", example = "17000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer originalPrice;
    @Schema(description = "도서 발행일", example = "2025-09-22", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate publishedDate;

    @Schema(description = "national library 에서 가져오면 true, 우리 데이터베이스에서 가져오면 false", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean foundInNationalLibrary;
}
