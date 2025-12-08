package com.bookbook.booklink.book_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@Schema(description = "도서 상세 조회를 위한 도서관 응답 DTO")
public class LibraryDto {
    private final UUID id;
    private final String name;
    private final Double latitude;
    private final Double longitude;
}
