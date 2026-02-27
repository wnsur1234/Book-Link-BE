package com.bookbook.booklink.s3.controller;

import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.s3.controller.docs.S3ApiDocs;
import com.bookbook.booklink.s3.model.dto.response.PresignedUrlRespDto;
import com.bookbook.booklink.s3.service.S3PresignedUrlService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class S3Controller implements S3ApiDocs {

    private final S3PresignedUrlService s3PresignedUrlService;

    @Value("${spring.cloud.aws.s3.url-duration}")
    private long urlDuration;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public ResponseEntity<BaseResponse<PresignedUrlRespDto>> getPresignedUrl(
            @RequestParam @NotNull(message = "저장할 이미지 이름은 필수입니다.") String fileName,
            @RequestHeader("Trace-Id") String traceId
    ) {
        // 5분 동안 유효
        String ROOT = "library-book-images/";
        String key = traceId + "/" + ROOT + fileName;
        URL url = s3PresignedUrlService.generatePresignedUrl(
                bucketName,
                key,
                Duration.ofMillis(urlDuration)
        );

        return ResponseEntity.ok()
                .body(BaseResponse.success(
                        PresignedUrlRespDto.builder()
                                .url(url.toString())
                                .key(key)
                                .build()
                ));
    }
}
