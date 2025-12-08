package com.bookbook.booklink.board_service.model.dto.response;

import com.bookbook.booklink.board_service.model.Board;
import com.bookbook.booklink.board_service.model.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 상세 조회 응답 DTO") // 👈 클래스 설명
public class BoardDetailDto {

    @Schema(description = "게시글 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Schema(description = "작성자 이름", example = "책읽는곰", accessMode = Schema.AccessMode.READ_ONLY)
    private String writerName;

    @Schema(description = "제목", example = "최근 독서 후기 공유합니다.", accessMode = Schema.AccessMode.READ_ONLY)
    private String title;

    @Schema(description = "내용", example = "내용이 길어질 수 있습니다.", accessMode = Schema.AccessMode.READ_ONLY)
    private String content;

    @Schema(description = "작성 시간", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "수정 여부", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isUpdated;

    @Schema(description = "좋아요 수", example = "25", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer commentCount;

    @Schema(description = "카테고리", example = "GENERAL", accessMode = Schema.AccessMode.READ_ONLY)
    private BoardCategory category;

    @Schema(description = "조회 수", example = "340", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer viewCount;

    @Schema(description = "현재 사용자가 작성자인지 여부", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isOwner;

    /**
     * Board 엔티티를 BoardDetailDto로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param board 변환할 Board 엔티티
     * @return BoardDetailDto 인스턴스
     */
    public static BoardDetailDto fromEntity(Board board, UUID currentUserId) {
        return BoardDetailDto.builder()
                .id(board.getId())
                .writerName(board.getWriterName())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .isUpdated(board.getUpdatedAt() != null)
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .category(board.getCategory())
                .viewCount(board.getViewCount())
                .isOwner(board.getMember().getId().equals(currentUserId))
                .build();
    }
}