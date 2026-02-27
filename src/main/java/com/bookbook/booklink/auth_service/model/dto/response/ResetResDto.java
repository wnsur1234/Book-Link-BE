package com.bookbook.booklink.auth_service.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 재설정 링크 발송 결과")
public class ResetResDto {

    @Schema(description = "전송 여부", example = "true")
    private boolean sent;

}
