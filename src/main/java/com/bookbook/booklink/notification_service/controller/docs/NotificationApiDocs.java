package com.bookbook.booklink.notification_service.controller.docs;

import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.notification_service.model.dto.response.NotificationResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification API", description = "알림 관련 API")
@RequestMapping("/api/notification")
public interface NotificationApiDocs {

    @Operation(
            summary = "회원의 모든 알림 조회",
            description = "회원에게 온 모든 알림을 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping
    public ResponseEntity<BaseResponse<List<NotificationResDto>>> getNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );

    @Operation(
            summary = "회원의 모든 읽지않은 알림 조회",
            description = "회원에게 온 모든 읽지않은 알림을 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @GetMapping("/unread")
    ResponseEntity<BaseResponse<List<NotificationResDto>>> getUnreadNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );

    @Operation(
            summary = "알림 읽음 처리 (단건)",
            description = "해당 알림을 읽음 처리합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.NOTIFICATION_NOT_FOUND})
    @PutMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> readNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );

    @Operation(
            summary = "모든 알림 읽음 처리",
            description = "회원에게 온 모든 읽지않은 알림을 읽음 처리합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @PutMapping("/read-all")
    ResponseEntity<BaseResponse<Boolean>> readAllNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );

    @Operation(
            summary = "알림 삭제 (단건)",
            description = "해당 알림을 삭제합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.NOTIFICATION_NOT_FOUND})
    @DeleteMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> deleteNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );

    @Operation(
            summary = "회원의 모든 알림 삭제",
            description = "회원에게 온 모든 알림을 삭제합니다. (읽지않은 알림 포함)"
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @DeleteMapping
    ResponseEntity<BaseResponse<Boolean>> deleteAllNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );

    @Operation(
            summary = "회원의 모든 읽은 알림 삭제",
            description = "회원에게 온 모든 읽은 알림을 삭제합니다. (읽지않은 알림 미포함)"
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION})
    @DeleteMapping("/read")
    ResponseEntity<BaseResponse<Boolean>> deleteAllReadNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    );
}
