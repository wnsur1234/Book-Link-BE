package com.bookbook.booklink.notification_service.controller;

import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.notification_service.controller.docs.NotificationApiDocs;
import com.bookbook.booklink.notification_service.model.dto.response.NotificationResDto;
import com.bookbook.booklink.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApiDocs {
    private final NotificationService notificationService;

    @Override
    public ResponseEntity<BaseResponse<List<NotificationResDto>>> getNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {

        List<NotificationResDto> notificationList = notificationService.getNotifications(userId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(notificationList));
    }

    @Override
    public ResponseEntity<BaseResponse<List<NotificationResDto>>> getUnreadNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {
        List<NotificationResDto> notificationList = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(notificationList));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> readNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {
        log.info("[NotificationController] [userId = {}] Read Notification request received. notificationId={}",
                userId, id);

        notificationService.readNotification(id, userId);

        log.info("[NotificationController] [userId = {}] Read Notification request success. notificationId={}",
                userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> readAllNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {

        log.info("[NotificationController] [userId = {}] Read aAll Notification request received.",
                userId);

        notificationService.readAllNotifications(userId);

        log.info("[NotificationController] [userId = {}] Read all Notification request success.",
                userId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteNotification(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {

        log.info("[NotificationController] [userId = {}] Delete Notification request received. notificationId={}",
                userId, id);

        notificationService.deleteNotification(id, userId);

        log.info("[NotificationController] [userId = {}] Delete Notification request success. notificationId={}",
                userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteAllNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {
        log.info("[NotificationController] [userId = {}] Delete all Notification request received.",
                userId);

        notificationService.deleteAllNotifications(userId);

        log.info("[NotificationController] [userId = {}] Delete all Notification request success.",
                userId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteAllReadNotifications(
            @AuthenticationPrincipal(expression = "member.id") UUID userId
    ) {
        log.info("[NotificationController] [userId = {}] Delete all read Notification request received.",
                userId);

        notificationService.deleteAllReadNotifications(userId);

        log.info("[NotificationController] [userId = {}] Delete all read Notification request success.",
                userId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }
}
    