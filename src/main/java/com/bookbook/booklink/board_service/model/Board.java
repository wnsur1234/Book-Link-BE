package com.bookbook.booklink.board_service.model;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.board_service.model.dto.request.BoardCreateDto;
import com.bookbook.booklink.board_service.model.dto.request.BoardUpdateDto;
import com.bookbook.booklink.comment_service.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글(Board) 엔티티 정보")
public class Board {
    /**
     * 게시글의 고유 식별자 (UUID).
     * 데이터베이스에 저장될 때 자동으로 생성되며, 수정 불가능합니다.
     */
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    @Schema(description = "게시글 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    /**
     * 작성자 이름.
     * 회원의 이름과 동기화되나, 게시글 작성 시점에 저장되어 회원의 이름 변경 시 영향을 받지 않습니다.
     */
    @Column(length = 100, nullable = false)
    @Schema(description = "작성자 이름", example = "홍길동")
    private String writerName;

    /**
     * 게시글 제목.
     */
    @Column(length = 255, nullable = false)
    @Schema(description = "게시글 제목", example = "제목입니다.")
    private String title;

    /**
     * 게시글 내용. 대용량 텍스트 저장을 위해 @Lob 사용.
     */
    @Lob
    @Column(nullable = false)
    @Schema(description = "게시글 내용", example = "이것은 게시글 본문 내용입니다.")
    private String content;

    /**
     * 생성 시각. 엔티티 생성 시 자동으로 기록됩니다.
     */
    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "생성 시각", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * 최종 수정 시각. 엔티티 수정 시 자동으로 갱신됩니다.
     */
    @LastModifiedDate
    @Schema(description = "최종 수정 시각", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    /**
     * 삭제 시각. 논리적 삭제(Soft Delete)에 사용됩니다.
     */
    @Schema(description = "삭제 시각 (null이면 삭제되지 않음)", nullable = true, accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime deletedAt;

    /**
     * 좋아요 수.
     */
    @Builder.Default
    @Min(0)
    @Schema(description = "좋아요 수", example = "15", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer likeCount = 0;

    /**
     * 댓글 수.
     */
    @Builder.Default
    @Min(0)
    @Schema(description = "댓글 수", example = "8", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer commentCount = 0;

    /**
     * 게시글 카테고리.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "게시글 카테고리", example = "NOTICE")
    private BoardCategory category;

    /**
     * 조회수.
     */
    @Builder.Default
    @Min(0)
    @Schema(description = "조회수", example = "120", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer viewCount = 0;

    /**
     * 게시글 작성자 (Member)와의 관계.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * 이 게시글에 달린 댓글 목록 (양방향 매핑).
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> commentList = new ArrayList<>();

    /**
     * 이 게시글에 대한 '좋아요' 목록 (양방향 매핑).
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardLikes> likesList = new ArrayList<>();

    /**
     * BoardCreateDto와 Member 정보를 사용하여 새로운 Board 엔티티를 생성합니다.
     *
     * @param boardCreateDto 생성 요청 DTO
     * @param member         게시글을 작성하는 사용자 엔티티
     * @return 생성된 Board 엔티티
     */
    public static Board toEntity(BoardCreateDto boardCreateDto, Member member) {
        return Board.builder()
                .writerName(member.getName())
                .title(boardCreateDto.getTitle())
                .content(boardCreateDto.getContent())
                .category(boardCreateDto.getCategory())
                .member(member)
                .build();

    }

    /**
     * 게시글의 제목과 내용을 수정합니다.
     *
     * @param boardUpdateDto 수정 요청 DTO (제목, 내용 포함)
     */
    public void update(BoardUpdateDto boardUpdateDto) {
        this.title = boardUpdateDto.getTitle();
        this.content = boardUpdateDto.getContent();
    }

    /**
     * 게시글을 논리적으로 삭제 처리합니다 (Soft Delete).
     * deletedAt 필드에 현재 시각을 기록합니다.
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 게시글 조회수를 1 증가시킵니다.
     */
    public void view() {
        this.viewCount++;
    }

    /**
     * 게시글의 좋아요 수를 1 증가시킵니다.
     */
    public void like() {
        this.likeCount++;
    }

    /**
     * 게시글의 좋아요 수를 1 감소시킵니다.
     * 좋아요 수가 0보다 클 때만 감소시킵니다.
     */
    public void unlike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 게시글의 댓글 수를 1 증가시킵니다.
     */
    public void comment() {
        this.commentCount++;
    }

    /**
     * 게시글의 댓글 수를 1 감소시킵니다.
     */
    public void uncomment() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}