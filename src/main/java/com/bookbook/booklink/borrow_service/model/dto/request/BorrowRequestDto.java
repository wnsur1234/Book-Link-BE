package com.bookbook.booklink.borrow_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
@Schema(description = "대여 요청 DTO")
public class BorrowRequestDto {

    @Schema(description = "도서관별 도서 id", example = "e712efc7-ab47-46e9-aa02-22bb401ec3f1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "도서관별 도서 id는 필수입니다.")
    UUID libraryBookId;

    @Schema(description = "반납 일자", example = "2025-10-14T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "반납 일자는 필수입니다.")
    @Future(message = "반납 일자는 미래여야 합니다.")
    LocalDateTime expectedReturnDate;
}
