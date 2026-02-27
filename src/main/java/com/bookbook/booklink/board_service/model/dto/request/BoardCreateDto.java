package com.bookbook.booklink.board_service.model.dto.request;

import com.bookbook.booklink.board_service.model.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "게시글 생성 요청 DTO")
public class BoardCreateDto {

    @NotBlank(message = "제목은 비어있을 수 없습니다.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    @Schema(description = "게시글 제목", example = "새로운 책 추천 요청합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    @Schema(description = "게시글 내용", example = "최근 재미있게 읽은 책이 있으시다면 추천해주세요!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull(message = "카테고리는 필수 선택 항목입니다.")
    @Schema(description = "게시글 카테고리", example = "GENERAL", requiredMode = Schema.RequiredMode.REQUIRED)
    private BoardCategory category;
}