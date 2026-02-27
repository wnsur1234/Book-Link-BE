package com.bookbook.booklink.common.dto;


import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.exception.ErrorInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;


@Getter
public class BaseResponse<T> {
    public static final String ERROR_RESPONSE = """
            {
              "success": false,
              "data": null,
              "error": {
                "timestamp": "2025-09-20T07:54:27.043Z",
                "status": 400,
                "code": "EXAMPLE_ERROR_CODE_400",
                "message": "에러 안내 메시지",
                "path": "api 경로"
              }
            }
            """;
    public static final String SUCCESS_RESPONSE = """
            {
              "success": true,
              "data": "123e4567-e89b-12d3-a456-426614174000",
              "error": null
            }
            """;
    @Schema(example = "true")
    private boolean success;
    @Schema(description = "성공 데이터", nullable = true)
    private T data;
    @Schema(description = "에러 정보", example = "null")
    private ErrorInfo error;

    // private 생성자
    private BaseResponse(boolean success, T data, ErrorInfo error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // 성공 응답 생성
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, data, null);
    }

    // 실패 응답 생성: ErrorCode enum과 path, 상세 메시지 등을 받아 ErrorInfo 생성
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String path) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .timestamp(Instant.now())
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .build();
        return new BaseResponse<>(false, null, errorInfo);
    }

    public static <T> BaseResponse<T> error(String message, String path) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(null)
                .message(message)
                .path(path)
                .build();
        return new BaseResponse<>(false, null, errorInfo);
    }
}