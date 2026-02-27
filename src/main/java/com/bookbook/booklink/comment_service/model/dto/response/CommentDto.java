package com.bookbook.booklink.comment_service.model.dto.response;

import com.bookbook.booklink.comment_service.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(name = "CommentDto", description = "댓글 및 대댓글 응답 DTO")
public class CommentDto {

    @Schema(description = "댓글 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Schema(description = "댓글 내용", example = "좋은 글이네요!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "작성자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String writerName;

    @Schema(description = "댓글 작성 시간", example = "2025-09-30T20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "댓글 수정 여부", example = "true")
    private Boolean isUpdated;

    @Schema(description = "좋아요 수", example = "5")
    private Integer likeCount;

    @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "true")
    private Boolean likedByMe;

    @Schema(description = "내가 작성한 댓글인지 여부", example = "true")
    private Boolean isMine;

    @Schema(
            description = "좋아요 기준 최상위 대댓글",
            implementation = CommentDto.class
    )
    private CommentDto topChild;

    public static CommentDto fromEntity(Comment comment, UUID userId) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writerName(comment.getWriterName())
                .createdAt(comment.getCreatedAt())
                .isUpdated(comment.getUpdatedAt() != null)
                .likeCount(comment.getLikeCount())
                .likedByMe(comment.getLikesList().stream()
                        .anyMatch(like -> like.getUserId().equals(userId)))
                .isMine(comment.getWriterId().equals(userId))
                .topChild(getTopChild(comment, userId))
                .build();
    }

    private static CommentDto getTopChild(Comment parent, UUID userId) {
        return parent.getChildren().stream()
                .filter(c -> c.getDeletedAt() == null)
                .max((c1, c2) -> {
                    int cmp = c1.getLikeCount().compareTo(c2.getLikeCount());
                    if (cmp != 0) return cmp;
                    return c2.getCreatedAt().compareTo(c1.getCreatedAt());
                })
                .map(c -> CommentDto.fromEntity(c, userId))
                .orElse(null);
    }
}
