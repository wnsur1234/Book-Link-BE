package com.bookbook.booklink.library_service.model.dto.response;

import com.bookbook.booklink.book_service.model.Book;
import com.bookbook.booklink.book_service.model.BookCategory;
import com.bookbook.booklink.book_service.model.LibraryBook;
import com.bookbook.booklink.library_service.model.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryDetailDto {

    @Schema(description = "도서관 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Schema(description = "도서관 이름", example = "강남 책방", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 20)
    private String name;

    @Schema(description = "도서관 소개", example = "강남에 위치한 아늑한 독립 서점입니다.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 200)
    private String description;

    @Schema(description = "도서관 리뷰 별점 평균", example = "4.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double stars;

    @Schema(description = "도서관이 받은 좋아요의 수", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer likeCount;

    @Schema(description = "도서관이 보유한 책의 수", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer bookCount;

    @Schema(description = "도서관 생성 일자", example = "2025-09-19T23:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "도서관 썸네일 URL", example = "https://example.com/thumbnail.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String thumbnailUrl;

    @Schema(description = "영업 시작 시간", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime startTime;

    @Schema(description = "영업 종료 시간", example = "21:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime endTime;

    @Schema(description = "이 도서관에서 좋아요 수가 높은 상위 5권의 도서")
    private List<PopularBookDto> topBooks;

    @Schema(description = "현재 위치에서 도서관까지의 거리 (km)", example = "1.25")
    private Double distanceKm;

    public static LibraryDetailDto fromEntity(LibraryDistanceProjection projection) {
        Library library = projection.getLibrary();
        Double distance = projection.getDistance();
        return LibraryDetailDto.builder()
                .id(library.getId())
                .name(library.getName())
                .description(library.getDescription())
                .stars(library.getStars())
                .likeCount(library.getLikeCount())
                .bookCount(library.getBookCount())
                .createdAt(library.getCreatedAt())
                .thumbnailUrl(library.getThumbnailUrl())
                .startTime(library.getStartTime())
                .endTime(library.getEndTime())
                .distanceKm(distance)
                .build();
    }

    public static LibraryDetailDto fromEntity(Library library, List<LibraryBook> top5List) {
        return LibraryDetailDto.builder()
                .id(library.getId())
                .name(library.getName())
                .description(library.getDescription())
                .stars(library.getStars())
                .likeCount(library.getLikeCount())
                .bookCount(library.getBookCount())
                .createdAt(library.getCreatedAt())
                .thumbnailUrl(library.getThumbnailUrl())
                .startTime(library.getStartTime())
                .topBooks(top5List.stream()
                        .map(PopularBookDto::from)
                        .collect(Collectors.toList()))
                .endTime(library.getEndTime())
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PopularBookDto {
        @Schema(description = "도서 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID bookId;

        @Schema(description = "도서 제목", example = "마흔에 읽는 쇼펜하우어")
        private String title;

        @Schema(description = "저자명", example = "강용수")
        private String author;

        @Schema(description = "출판사", example = "유노북스", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 16)
        private String publisher;

        @Schema(description = "카테고리", example = "GENERALITIES", requiredMode = Schema.RequiredMode.REQUIRED)
        private BookCategory category;

        public static PopularBookDto from(LibraryBook libraryBook) {
            Book book = libraryBook.getBook();
            return PopularBookDto.builder()
                    .bookId(libraryBook.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .publisher(book.getPublisher())
                    .category(book.getCategory())
                    .build();
        }
    }
}
