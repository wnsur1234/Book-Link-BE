package com.bookbook.booklink.library_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"library_id", "userId"})
        }
)
@Schema(description = "게시글 좋아요(BoardLikes) 엔티티: 사용자가 게시글에 좋아요를 누른 기록")
public class LibraryLikes {

    /**
     * 좋아요 기록의 고유 식별자 (UUID).
     */
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "좋아요 기록 고유 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    /**
     * 좋아요가 남겨진 도서관
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library_id", nullable = false)
    @NotNull
    private Library library;

    /**
     * 좋아요를 누른 사용자의 고유 ID.
     */
    @Column(nullable = false)
    @NotNull(message = "사용자 ID는 필수입니다.")
    @Schema(description = "좋아요를 누른 사용자 ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID userId;

    /**
     * 좋아요를 누른 시각.
     */
    @Column(nullable = false)
    @CreationTimestamp
    @Schema(description = "좋아요 생성 시각", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * Board와 사용자 ID를 사용하여 새로운 BoardLikes 엔티티를 생성합니다.
     *
     * @param library 좋아요를 받을 도서관
     * @param userId  좋아요를 누른 사용자의 고유 ID
     * @return 생성된 BoardLikes 엔티티
     */
    public static LibraryLikes create(Library library, UUID userId) {
        return LibraryLikes.builder()
                .library(library)
                .userId(userId)
                .build();
    }
}
