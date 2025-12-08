package com.bookbook.booklink.book_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewImage {
    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    @Schema(description = "미리보기 이미지 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    private UUID id;

    @Column(nullable = false, length = 2048)
    @Schema(description = "미리보기 이미지 경로", example = "https://{버킷이름}.s3.{리전}.amazonaws.com/{파일경로}", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageUrl;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library_book_id", nullable = false)
    @Schema(description = "도서관별 도서 정보")
    private LibraryBook libraryBook;

    public static PreviewImage toEntity(String imageUrl) {
        return PreviewImage.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
