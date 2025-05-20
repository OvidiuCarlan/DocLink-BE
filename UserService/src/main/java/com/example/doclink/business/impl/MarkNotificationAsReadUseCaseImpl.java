package com.example.doclink.business.impl;

import com.example.doclink.business.cases.MarkNotificationAsReadUseCase;
import com.example.doclink.persistance.NotificationRepository;
import com.example.doclink.persistance.entity.NotificationEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MarkNotificationAsReadUseCaseImpl implements MarkNotificationAsReadUseCase {

    private final NotificationRepository notificationRepository;

    @Override
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}