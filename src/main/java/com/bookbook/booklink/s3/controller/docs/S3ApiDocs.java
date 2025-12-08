package com.bookbook.booklink.s3.controller.docs;

import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.s3.model.dto.response.PresignedUrlRespDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "S3 API", description = "s3 이미지 저장 관련 api")
@RequestMapping("/api/s3")
public interface S3ApiDocs {

    @Operation(
            summary = "presigned-url 요청",
            description = "s3에서 presigned-url 요청해서 이미지를 저장할 url 경로를 반환합니다."
    )
    @ApiErrorResponses({})
    @GetMapping("/presigned-url")
    public ResponseEntity<BaseResponse<PresignedUrlRespDto>> getPresignedUrl(
            @RequestParam @NotNull(message = "저장할 이미지 이름은 필수입니다.") String fileName,
            @RequestHeader("Trace-Id") String traceId
    );
}
