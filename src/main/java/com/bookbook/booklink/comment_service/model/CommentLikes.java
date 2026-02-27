package com.bookbook.booklink.comment_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
                @UniqueConstraint(columnNames = {"comment_id", "userId"})
        }
)
@Schema(name = "CommentLikes", description = "댓글 좋아요 엔티티")
public class CommentLikes {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "댓글 좋아요 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    @Schema(description = "좋아요가 남겨진 댓글")
    private Comment comment;

    @Column(nullable = false)
    @Schema(description = "좋아요를 누른 사용자 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID userId;

    @Column(nullable = false)
    @CreationTimestamp
    @Schema(description = "좋아요를 누른 시간", example = "2025-09-30T20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    public static CommentLikes create(Comment comment, UUID userId) {
        return CommentLikes.builder()
                .comment(comment)
                .userId(userId)
                .build();
    }
}
