package com.bookbook.booklink.comment_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "댓글 수정 요청 DTO")
public class CommentUpdateDto {

    @NotNull(message = "댓글 ID는 필수입니다.")
    @Schema(description = "수정할 댓글의 고유 ID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;

    @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    @Schema(description = "수정할 댓글 내용", example = "수정된 댓글 내용입니다.")
    private String content;
}