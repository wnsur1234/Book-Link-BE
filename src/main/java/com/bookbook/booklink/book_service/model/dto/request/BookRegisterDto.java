package com.bookbook.booklink.book_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "도서 등록 요청 DTO")
public class BookRegisterDto {
    @Schema(description = "도서 이름", example = "마흔에 읽는 쇼펜하우어", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "도서 이름은 필수입니다.")
    @Size(min = 1, max = 64, message = "도서 이름은 1자 이상 64자 이하이어야 합니다.")
    String title;

    @Schema(description = "저자명", example = "강용수", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "저자 이름은 필수입니다.")
    @Size(min = 1, max = 16, message = "저자 이름은 1자 이상 16자 이하이어야 합니다.")
    String author;

    @Schema(description = "출판사", example = "유노북스", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "출판사 이름은 필수입니다.")
    @Size(min = 1, max = 16, message = "출판사는 1자 이상 16자 이하이어야 합니다.")
    String publisher;

    @Schema(description = "카테고리", example = "000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "카테고리는 필수입니다.")
    @Size(min = 3, max = 3, message = "KDC(한국십진분류법) 대분류 코드는 세글자 입니다.")
    String category;

    @Schema(description = "isbn 코드", example = "9791192300818", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "isbn 코드는 필수입니다.")
    String isbn;

    @Schema(description = "정가", example = "17000")
    @Min(value = 0, message = "도서 정가는 양수여야 합니다.")
    Integer originalPrice;

    @Schema(description = "발행일", example = "20230903")
    LocalDate publishedDate;
}
