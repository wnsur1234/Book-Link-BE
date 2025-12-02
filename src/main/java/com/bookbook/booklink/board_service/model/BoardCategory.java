package com.bookbook.booklink.board_service.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 카테고리 유형")
public enum BoardCategory {
    @Schema(description = "일상 게시판")
    DAILY,
    @Schema(description = "모임 모집 게시판")
    GATHER,
    @Schema(description = "책 추천 게시판")
    RECOMMEND
}