package com.bookbook.booklink.chat_service.single.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SingleRoomReqDto {

    @Schema(description = "참여자 2 ID", example = "7fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID chatPartner;
}
