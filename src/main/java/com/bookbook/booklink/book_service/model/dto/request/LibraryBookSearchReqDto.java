package com.bookbook.booklink.book_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "도서 검색/조회 요청 DTO")
public class LibraryBookSearchReqDto {

    @Schema(description = "위도", example = "37.48486731057572", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Double latitude;

    @Schema(description = "경도", example = "126.92841740891708", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Double longitude;

    @Schema(description = "도서관 ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    UUID libraryId;

    @Schema(description = "페이지 번호", example = "0", defaultValue = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0; // 기본값 0

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    private int size = 10; // 기본값 10

    @Schema(description = "검색어(책 제목)", example = "마흔에 읽는 쇼펜하우어", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bookName; // 검색어 (nullable)

    @Schema(description = "정렬 기준", example = "DISTANCE or LATEST or MOST_BORROWED", defaultValue = "DISTANCE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LibraryBookSortType sortType = LibraryBookSortType.DISTANCE; // 정렬 조건, 기본값 거리순

}