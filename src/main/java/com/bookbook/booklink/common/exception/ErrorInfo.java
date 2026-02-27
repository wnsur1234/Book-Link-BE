package com.bookbook.booklink.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@Schema(description = "에러 상세 정보")
public class ErrorInfo {

    @Schema(description = "에러 발생 시간", example = "2025-09-20T07:54:27.043Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "에러 코드", example = "LIBRARY_NOT_FOUND_400", implementation = ErrorCode.class)
    private String code;

    @Schema(description = "사용자 친화적 메시지", example = "해당 ID의 도서관이 존재하지 않습니다.")
    private String message;

    @Schema(description = "요청 경로", example = "/api/library")
    private String path;
}
