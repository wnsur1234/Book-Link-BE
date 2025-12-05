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
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 목록 조회 응답 DTO")
public class BoardListDto {

    @Schema(description = "게시글 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Schema(description = "작성자 이름", example = "책사랑", accessMode = Schema.AccessMode.READ_ONLY)
    private String writerName;

    @Schema(description = "제목", example = "새로운 책 추천 요청합니다.", accessMode = Schema.AccessMode.READ_ONLY)
    private String title;

    @Schema(description = "내용 미리보기", example = "제가 관심있게 본 책이 있는데, 해당 책의 특징은 ...", accessMode = Schema.AccessMode.READ_ONLY)
    private String previewContent;

    @Schema(description = "작성 시간", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "좋아요 수", example = "15", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer likeCount;

    @Schema(description = "댓글 수", example = "8", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer commentCount;

    @Schema(description = "카테고리", example = "GENERAL", accessMode = Schema.AccessMode.READ_ONLY)
    private BoardCategory category;

    @Schema(description = "조회 수", example = "120", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer viewCount = 0;

    /**
     * Board 엔티티를 BoardListDto로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param board 변환할 Board 엔티티
     * @return BoardListDto 인스턴스
     */
    public static BoardListDto fromEntity(Board board) {
        return BoardListDto.builder()
                .id(board.getId())
                .writerName(board.getWriterName())
                .title(board.getTitle())
                .createdAt(board.getCreatedAt())
                .likeCount(board.getLikeCount())
                .commentCount(board.getCommentCount())
                .category(board.getCategory())
                .viewCount(board.getViewCount())
                .previewContent(board.getContent().substring(0, Math.min(board.getContent().length() - 1, 60)))
                .build();
    }

}