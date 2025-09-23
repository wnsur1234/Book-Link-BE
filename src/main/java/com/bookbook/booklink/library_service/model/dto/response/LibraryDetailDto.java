package com.bookbook.booklink.library_service.model.dto.response;

import com.bookbook.booklink.library_service.model.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

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
    private Integer like_count;

    @Schema(description = "도서관이 보유한 책의 수", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer book_count;

    @Schema(description = "도서관 생성 일자", example = "2025-09-19T23:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime created_at;

    @Schema(description = "도서관 썸네일 URL", example = "https://example.com/thumbnail.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String thumbnail_url;

    @Schema(description = "영업 시작 시간", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime start_time;

    @Schema(description = "영업 종료 시간", example = "21:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime end_time;

    public static LibraryDetailDto fromEntity(Library library) {
        return LibraryDetailDto.builder()
                .id(library.getId())
                .name(library.getName())
                .description(library.getDescription())
                .stars(library.getStars())
                .like_count(library.getLike_count())
                .book_count(library.getBook_count())
                .created_at(library.getCreated_at())
                .thumbnail_url(library.getThumbnail_url())
                .start_time(library.getStart_time())
                .end_time(library.getEnd_time())
                .build();
    }
}
