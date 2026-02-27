package com.bookbook.booklink.library_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalTime;

@Getter
@Schema(description = "도서관 등록 요청 DTO")
public class LibraryRegDto {

    @Schema(description = "도서관 이름", example = "책책도서관", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "도서관 이름은 필수입니다.")
    @Size(min = 2, max = 20, message = "도서관 이름은 2자 이상 20자 이하이어야 합니다.")
    private String name;

    @Schema(description = "도서관 소개 (간단한 설명 또는 특징 포함)", example = "지역 주민들이 자유롭게 책을 빌리고 모임을 할 수 있는 따뜻한 공간입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "도서관 소개는 필수입니다.")
    @Size(min = 10, max = 200, message = "도서관 소개는 10자 이상 200자 이하이어야 합니다.")
    private String description;

    @Schema(description = "도서관 대표 썸네일 이미지 URL", example = "https://example.com/images/library-thumbnail.jpg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @URL(message = "썸네일은 올바른 URL 형식이어야 합니다.")
    private String thumbnailUrl;

    @Schema(description = "도서관 운영 시작 시간 (HH:mm 형식)", type = "string", format = "partial-time", example = "09:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalTime startTime;

    @Schema(description = "도서관 운영 종료 시간 (HH:mm 형식)", type = "string", format = "partial-time", example = "21:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalTime endTime;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(description = "도서관 위도", example = "37.497923", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(description = "도서관 경도", example = "127.027612", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @AssertTrue(message = "영업 시작 시간은 종료 시간보다 빨라야 합니다.")
    public boolean isValidOperatingHours() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return startTime.isBefore(endTime);
    }
}
