package com.bookbook.booklink.comment_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(name = "CommentCreateDto", description = "댓글 생성 요청 DTO")
public class CommentCreateDto {

    @NotNull(message = "게시글 ID는 필수입니다.")
    @Schema(description = "댓글이 속한 게시글 ID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID boardId;

    @Schema(description = "부모 댓글 ID (null이면 최상위 댓글)",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID parentId;

    @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    @Schema(description = "댓글 내용",
            example = "좋은 글이에요!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}