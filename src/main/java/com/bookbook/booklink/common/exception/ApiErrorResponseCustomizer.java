package com.bookbook.booklink.common.exception;

import com.bookbook.booklink.common.exception.ErrorCode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;

@Component
public class ApiErrorResponseCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiErrorResponses annotation = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);
        if (annotation == null) return operation;

        ApiResponses apiResponses = operation.getResponses();

        for (ErrorCode errorCode : annotation.value()) {
            String statusCode = String.valueOf(errorCode.getHttpStatus().value());

            // 이미 등록된 ApiResponse 가져오기 (없으면 새로 생성)
            ApiResponse apiResponse = apiResponses.get(statusCode);
            if (apiResponse == null) {
                apiResponse = new ApiResponse()
                        .description(errorCode.getHttpStatus().getReasonPhrase()) // "Bad Request", "Internal Server Error" 등
                        .content(new Content().addMediaType("application/json", new MediaType()));
                apiResponses.addApiResponse(statusCode, apiResponse);
            }

            // MediaType 안에 examples 추가
            MediaType mediaType = apiResponse.getContent().get("application/json");
            mediaType.addExamples(
                    errorCode.name(), // enum 이름을 키로
                    new Example().value("""
                    {
                      "success": false,
                      "data": null,
                      "error": {
                        "timestamp": "2025-09-20T07:54:27.043Z",
                        "status": %d,
                        "code": "%s",
                        "message": "%s",
                        "path": "/api/..."
                      }
                    }
                    """.formatted(
                            errorCode.getHttpStatus().value(),
                            errorCode.getCode(),
                            errorCode.getMessage()
                    ))
            );
        }


        return operation;
    }
}
