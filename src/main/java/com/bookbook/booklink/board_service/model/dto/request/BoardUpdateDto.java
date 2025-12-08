package com.bookbook.booklink.board_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "게시글 수정 요청 DTO")
public class BoardUpdateDto {

    @NotNull(message = "게시글 ID는 필수입니다.")
    @Schema(description = "수정할 게시글의 고유 ID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID boardId;

    @NotBlank(message = "제목은 비어있을 수 없습니다.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    @Schema(description = "수정할 게시글 제목", example = "수정된 책 추천 글입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    @Schema(description = "수정할 게시글 내용", example = "내용을 수정했습니다. 참고 바랍니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

}