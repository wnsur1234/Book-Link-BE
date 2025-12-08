package com.bookbook.booklink.book_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Schema(description = "도서관별 도서 등록 요청 DTO")
public class LibraryBookRegisterDto {
    @Schema(description = "도서 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "도서 ID는 필수입니다.")
    UUID id;

    @NotNull(message = "도서 설명은 필수입니다.")
    @Schema(description = "도서 설명", example = "도서 상태 상급의 깨끗한 도서로, 쾌적하게 이용 가능합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    String description;

    @Schema(description = "보유 권수", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "보유 권수는 필수입니다.")
    @Min(value = 0, message = "보유한 도서 개수는 양수여야 합니다.")
    Integer copies;

    @Schema(description = "보증금", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "보증금 가격은 필수입니다.")
    @Min(value = 0, message = "보증금 가격은 양수여야 합니다.")
    Integer deposit;

    @Schema(description = "이미지 url 목록", example = "[https://bookbook-booklink.s3.ap-northeast-2.amazonaws.com/doinlkxjoi-di9u09/library-book-images/image.jpg]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    List<String> previewImages;
}
