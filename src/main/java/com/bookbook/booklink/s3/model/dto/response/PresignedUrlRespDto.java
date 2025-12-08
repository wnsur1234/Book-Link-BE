package com.bookbook.booklink.s3.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlRespDto {
    String url;
    String key;
}
