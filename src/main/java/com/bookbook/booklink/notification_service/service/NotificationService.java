package com.bookbook.booklink.notification_service.service;

import com.bookbook.booklink.auth_service.service.MemberService;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.notification_service.model.Notification;
import com.bookbook.booklink.notification_service.model.dto.request.NotificationCreateDto;
import com.bookbook.booklink.notification_service.model.dto.response.NotificationResDto;
import com.bookbook.booklink.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * 사용자의 모든 알림 조회
     * <p>
     * 주의: 단순히 모든 알림을 반환하므로 읽음 여부와 관계없이 포함됨.
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 알림 리스트 (최신순 정렬은 아님)
     */
    @Transactional(readOnly = true)
    public List<NotificationResDto> getNotifications(UUID userId) {

        List<Notification> notificationList = notificationRepository.findAllByMember_IdOrderByCreatedAtDesc(userId);

        return notificationList.stream().map(NotificationResDto::fromEntity).toList();
    }

    /**
     * 사용자의 모든 읽지 않은(unread) 알림 조회
     * <p>
     * 클라이언트에서 뱃지 카운트나 새 알림 목록 표시할 때 사용.
     *
     * @param userId 조회할 사용자의 ID
     * @return 읽지 않은 알림 리스트 (생성일 기준 내림차순 정렬)
     */
    @Transactional(readOnly = true)
    public List<NotificationResDto> getUnreadNotifications(UUID userId) {

        List<Notification> notificationList = notificationRepository.findAllByMember_IdAndIsReadFalseOrderByCreatedAtDesc(userId);

        return notificationList.stream().map(NotificationResDto::fromEntity).toList();
    }

    /**
     * 단일 알림 읽음 처리
     * <p>
     * - 멱등성 키를 통해 동일 요청이 반복 실행되지 않도록 보장.
     * - 알림 엔티티의 isRead 플래그를 true로 변경 후 저장.
     *
     * @param notificationId 읽음 처리할 알림 ID
     * @param userId         요청한 사용자 ID
     */
    @Transactional
    public void readNotification(UUID notificationId, UUID userId) {
        log.info("[NotificationService]  [userId = {}]  Marking notification as read. notificationId={}",
                userId, notificationId);

        int updatedCount = notificationRepository.markAsReadByIdAndMember_Id(notificationId, userId);

        if (updatedCount == 0) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        log.info("[NotificationService] [ userId = {}] Notification marked as read successfully. notificationId={}",
                userId, notificationId);
    }

    /**
     * 사용자의 모든 알림을 일괄 읽음 처리
     * <p>
     * - 단건 루프가 아닌 bulk update 쿼리로 성능 최적화.
     * - 클라이언트에서 "모두 읽음" 버튼을 눌렀을 때 사용.
     *
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void readAllNotifications(UUID userId) {
        log.info("[NotificationService]  [userId = {}] Marking all notifications as read.",
                userId);

        int updatedCount = notificationRepository.markAllAsReadByMember_Id(userId);

        if (updatedCount == 0) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        log.info("[NotificationService] [userId = {}]  All notifications marked as read successfully. ",
                userId);
    }

    /**
     * 단일 알림 삭제
     * <p>
     * - DB에서 완전히 삭제(hard delete).
     *
     * @param notificationId 삭제할 알림의 ID
     * @param userId         요청한 사용자 ID
     */
    @Transactional
    public void deleteNotification(UUID notificationId, UUID userId) {
        log.info("[NotificationService]  [userId = {}] Deleting notification. notificationId={}",
                userId, notificationId);

        // 알림 조회 후 삭제
        Notification notification = findNotificationById(notificationId, userId);
        notificationRepository.delete(notification);

        log.info("[NotificationService] [userId = {}] Notification deleted successfully. notificationId={}",
                userId, notificationId);
    }

    /**
     * 사용자의 모든 알림 삭제
     * <p>
     * - DB에서 해당 userId의 모든 알림을 삭제.
     *
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void deleteAllNotifications(UUID userId) {
        log.info("[NotificationService]  [userId = {}] Deleting all notifications.", userId);

        notificationRepository.deleteAllByMember_Id(userId);

        log.info("[NotificationService]  [userId = {}] All notifications deleted successfully.", userId);
    }


    /**
     * 사용자의 모든 읽은 알림 삭제
     * <p>
     * - 읽은 알림(isRead=true)만 선별적으로 삭제.
     * - 안 읽은 알림은 유지되므로 유저 경험을 보존.
     *
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void deleteAllReadNotifications(UUID userId) {
        log.info("[NotificationService] [userId = {}] Deleting all read notifications.", userId);

        notificationRepository.deleteAllByMember_IdAndIsReadTrue(userId);

        log.info("[NotificationService] [userId = {}] All read notifications deleted successfully.", userId);
    }

    /**
     * 알림 생성
     * <p>
     * - 전달받은 정보를 바탕으로 Notification 엔티티 생성 후 저장.
     * - 추후 웹소켓, SSE 등을 통해 실시간으로 클라이언트에게 전송하는 확장 포인트.
     *
     * @param notificationCreateDto 알림 생성에 필요한 정보 (userId, type, relatedId 등)
     */
    @Transactional
    public void sendNotification(NotificationCreateDto notificationCreateDto) {
        String destination = "/sub/notification/" + notificationCreateDto.getUserId();

        log.info("[NotificationService] Creating new notification. userId={}, type={}, relatedId={}",
                notificationCreateDto.getUserId(),
                notificationCreateDto.getType(),
                notificationCreateDto.getRelatedId());


        Notification newNotification = Notification.toEntity(notificationCreateDto, memberService.getMemberOrThrow(notificationCreateDto.getUserId()));
        notificationRepository.save(newNotification);

        messagingTemplate.convertAndSend(destination, NotificationResDto.fromEntity(newNotification));

        log.info("[NotificationService] Notification created successfully. notificationId={}", newNotification.getId());
    }

    /**
     * ID 기반 단일 알림 조회
     * - 없을 경우 CustomException(ErrorCode.NOTIFICATION_NOT_FOUND) 발생.
     *
     * @param notificationId 조회할 알림의 ID
     * @return Notification 엔티티
     */
    public Notification findNotificationById(UUID notificationId, UUID userId) {
        return notificationRepository.findByIdAndMember_Id(notificationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

}
    