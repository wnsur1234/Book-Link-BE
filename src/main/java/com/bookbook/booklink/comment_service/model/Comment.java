package com.bookbook.booklink.comment_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.board_service.model.Board;
import com.bookbook.booklink.comment_service.model.dto.request.CommentCreateDto;
import com.bookbook.booklink.comment_service.model.dto.request.CommentUpdateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 댓글 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Comment", description = "댓글 엔티티")
public class Comment {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "댓글 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Column(nullable = false)
    @Size(min = 1, max = 300)
    @Schema(description = "댓글 내용", example = "이 책 정말 추천합니다!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Column(nullable = false)
    @CreationTimestamp
    @Schema(description = "댓글 작성 일시", example = "2025-09-30T20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Schema(description = "댓글 수정 일시", example = "2025-09-30T21:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "댓글 삭제 일시", example = "2025-09-30T22:00:00")
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "좋아요 수", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer likeCount = 0;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "댓글 작성자 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID writerId;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "댓글 작성자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String writerName;

    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "댓글 개수", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer commentCount = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    @Schema(description = "댓글이 속한 게시글")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Schema(description = "부모 댓글 (최상위 댓글이면 null)")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "대댓글 리스트")
    private List<Comment> children = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "댓글 좋아요 리스트")
    private List<CommentLikes> likesList = new ArrayList<>();

    public static Comment toEntity(CommentCreateDto commentCreateDto, Member member, Board board, Comment parent) {
        return Comment.builder()
                .content(commentCreateDto.getContent())
                .board(board)
                .writerName(member.getName())
                .writerId(member.getId())
                .parent(parent)
                .build();
    }

    public void update(CommentUpdateDto commentUpdateDto) {
        this.content = commentUpdateDto.getContent();
    }

    public void comment() {
        this.commentCount++;
    }

    public void uncomment() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void like() {
        this.likeCount++;
    }

    public void unlike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
