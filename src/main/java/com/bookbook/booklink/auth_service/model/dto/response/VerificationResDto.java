package com.bookbook.booklink.auth_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResDto {
    @Schema(description = "인증 성공 여부", example = "true")
    private boolean verified;

    public static VerificationResDto of(boolean v) {
        return new VerificationResDto(v);
    }
}
