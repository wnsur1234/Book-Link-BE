package com.bookbook.booklink.book_service.model;

import com.bookbook.booklink.book_service.model.dto.request.BookRegisterDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Book {
    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    @Schema(description = "도서 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    private UUID id;

    @Column(nullable = false, length = 64)
    @Size(min = 1, max = 64)
    @Schema(description = "도서 이름", example = "마흔에 읽는 쇼펜하우어", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 64)
    @Getter
    private String title;

    @Column(nullable = false, length = 16)
    @Size(min = 1, max = 16)
    @Schema(description = "저자명", example = "강용수", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 16)
    @Getter
    private String author;

    @Column(nullable = false, length = 16)
    @Size(min = 1, max = 16)
    @Schema(description = "출판사", example = "유노북스", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 16)
    @Getter
    private String publisher;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "카테고리", example = "GENERALITIES", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    private BookCategory category;

    @Column(unique = true, length = 13)
    @Schema(description = "ISBN 코드", example = "9791192300818", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 13, max = 13)
    private String ISBN;

    @Min(0)
    @Column(nullable = false)
    @Schema(description = "도서 정가", example = "17000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer originalPrice;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "좋아요 수", example = "14", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer likeCount = 0;

    @Column(nullable = false, updatable = false)
    @Schema(description = "도서 발행일", example = "2025-09-22T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime publishedDate;

    public static Book toEntity(BookRegisterDto dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .ISBN(dto.getIsbn())
                .publisher(dto.getPublisher())
                .originalPrice(dto.getOriginalPrice())
                .publishedDate(dto.getPublishedDate().atStartOfDay())
                .category(BookCategory.getByCode(dto.getCategory()))
                .build();
    }
}
    