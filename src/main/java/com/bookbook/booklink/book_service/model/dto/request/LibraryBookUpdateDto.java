package com.bookbook.booklink.book_service.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
@Schema(description = "도서관별 도서 수정 DTO")
public class LibraryBookUpdateDto {
    @Schema(description = "도서관별 도서 ID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "도서관별 도서 ID는 필수입니다.")
    UUID id;

    @Schema(description = "보유 권수", example = "3")
    @Min(value = 0, message = "보유한 도서 개수는 양수여야 합니다.")
    Integer copies;

    @Schema(description = "보증금", example = "1000")
    @Min(value = 0, message = "보증금 가격은 양수여야 합니다.")
    Integer deposit;

    @Schema(description = "미리보기 이미지(null 이면 수정 x, 값이 있으면 기존 이미지 전체 교체)", example = "[https://bookbook-booklink.s3.ap-northeast-2.amazonaws.com/doinlkxjoi-di9u09/library-book-images/image.jpg]")
    List<String> previewImages;
}
