package com.example.doclink.business.impl;

import com.example.doclink.business.cases.GetUserNotificationsUseCase;
import com.example.doclink.business.dto.NotificationResponse;
import com.example.doclink.business.dto.NotificationsResponse;
import com.example.doclink.persistance.NotificationRepository;
import com.example.doclink.persistance.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GetUserNotificationsUseCaseImpl implements GetUserNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationsResponse getUserNotifications(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        long unreadCount = notificationRepository.countByUserIdAndIsRead(userId, false);

        return NotificationsResponse.builder()
                .notifications(notificationResponses)
                .unreadCount(unreadCount)
                .build();
    }

    private NotificationResponse convertToResponse(NotificationEntity entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .isRead(entity.isRead())
                .appointmentId(entity.getAppointmentId())
                .build();
    }
}