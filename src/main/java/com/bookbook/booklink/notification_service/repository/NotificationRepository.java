package com.bookbook.booklink.notification_service.repository;

import com.bookbook.booklink.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByMember_IdOrderByCreatedAtDesc(UUID userId);

    Optional<Notification> findByIdAndMember_Id(UUID notificationId, UUID userId);

    List<Notification> findAllByMember_IdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.member.id = :userId AND n.isRead = false")
    int markAllAsReadByMember_Id(@Param("userId") UUID userId);

    void deleteAllByMember_Id(UUID userId);

    void deleteAllByMember_IdAndIsReadTrue(UUID userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.member.id = :userId")
    int markAsReadByIdAndMember_Id(@Param("notificationId") UUID notificationId, @Param("userId") UUID userId);
}
    