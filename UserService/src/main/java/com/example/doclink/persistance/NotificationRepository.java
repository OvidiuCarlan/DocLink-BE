package com.example.doclink.persistance;

import com.example.doclink.persistance.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationEntity> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, boolean isRead);
    long countByUserIdAndIsRead(Long userId, boolean isRead);
}