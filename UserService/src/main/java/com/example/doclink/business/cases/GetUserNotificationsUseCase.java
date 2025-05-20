package com.example.doclink.business.cases;

import com.example.doclink.business.dto.NotificationsResponse;

public interface GetUserNotificationsUseCase {
    NotificationsResponse getUserNotifications(Long userId);
}