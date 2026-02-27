package com.bookbook.booklink.chat_service.chat_mutual.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileAttachmentDto {

    @Schema(description = "파일 이름", example = "photo.png")
    private String fileName;

    @Schema(description = "파일 경로", example = "https://s3.bucket.com/photo.png")
    private String filePath;

    @Schema(description = "파일 크기(byte)", example = "2048")
    private Integer fileSize;
}
