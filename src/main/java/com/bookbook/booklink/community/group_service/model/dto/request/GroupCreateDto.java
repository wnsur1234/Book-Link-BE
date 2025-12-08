package com.bookbook.booklink.community.group_service.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "새 모임을 생성하기 위한 요청 DTO")
public class GroupCreateDto {

    @Schema(description = "모임 썸네일 이미지 URL 또는 경로 (선택 사항)", example = "http://example.com/images/default.png")
    private String thumbnail;

    @NotBlank(message = "모임 이름은 필수 입력 항목입니다.")
    @Schema(description = "모임 이름 (필수)", example = "책으로 연결되는 사람들", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "모임 설명은 필수 입력 항목입니다.")
    @Schema(description = "모임에 대한 상세 설명 (필수)", example = "매주 토요일, 최신 베스트셀러를 읽고 토론합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = "최대 참여 인원수는 필수 입력 항목입니다.")
    @Min(value = 1, message = "최소 인원수는 1명 이상이어야 합니다.")
    @Max(value = 30, message = "최대 인원수는 30명까지 가능합니다.")
    @Schema(description = "모임 최대 참여 인원수 (1~30명)", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maxCapacity;

    @Schema(description = "참가 비밀번호 (비공개 모임일 경우 입력)", example = "1234")
    private String password;
}