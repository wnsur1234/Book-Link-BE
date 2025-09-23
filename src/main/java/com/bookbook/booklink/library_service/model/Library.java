package com.bookbook.booklink.library_service.model;

import com.bookbook.booklink.library_service.model.dto.request.LibraryRegDto;
import com.bookbook.booklink.library_service.model.dto.request.LibraryUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Library {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "도서관 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Column(nullable = false, length = 20)
    @Size(min = 2, max = 20)
    @Schema(description = "도서관 이름", example = "강남 책방", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 20)
    private String name;

    @Column(nullable = false, length = 200)
    @Size(min = 10, max = 200)
    @Schema(description = "도서관 소개", example = "강남에 위치한 아늑한 독립 서점입니다.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 200)
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "별점은 0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", inclusive = true, message = "별점은 5 이하이어야 합니다.")
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "도서관 리뷰 별점 평균", example = "4.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double stars = 0.0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "도서관이 받은 리뷰의 수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer review_count = 0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "도서관이 받은 좋아요의 수", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer like_count = 0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "도서관이 보유한 책의 수", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer book_count = 0;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @Schema(description = "도서관 생성 일자", example = "2025-09-19T23:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime created_at;

    @Column
    @URL(message = "올바른 URL 형식이어야 합니다.")
    @Schema(description = "도서관 썸네일 URL", example = "https://example.com/thumbnail.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String thumbnail_url;

    @Column(nullable = false)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(description = "도서관 위도", example = "37.497923", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @Column(nullable = false)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(description = "도서관 경도", example = "127.027612", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @Column(nullable = false)
    @Schema(description = "영업 시작 시간", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime start_time;

    @Column(nullable = false)
    @Schema(description = "영업 종료 시간", example = "21:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime end_time;

    // todo: User와 1:1 연결 필요

    public static Library toEntity(LibraryRegDto libraryRegDto) {
        return Library.builder()
                .name(libraryRegDto.getName())
                .description(libraryRegDto.getDescription())
                .latitude(13.241400)
                .longitude(15.414000)   // todo: User에서 위치정보 가져와서 위도 경도 변환해서 넣어야함
                .start_time(libraryRegDto.getStart_time())
                .end_time(libraryRegDto.getEnd_time())
                //todo: User 연결 추가
                .thumbnail_url(libraryRegDto.getThumbnail_url())
                .build();
    }

    public void updateLibraryInfo(LibraryUpdateDto libraryUpdateDto) {
        this.name = libraryUpdateDto.getName();
        this.description = libraryUpdateDto.getDescription();
        this.thumbnail_url = libraryUpdateDto.getThumbnail_url();
        this.start_time = libraryUpdateDto.getStart_time();
        this.end_time = libraryUpdateDto.getEnd_time();
    }
}