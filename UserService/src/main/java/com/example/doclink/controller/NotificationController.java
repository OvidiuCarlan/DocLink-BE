package com.example.doclink.controller;

import com.example.doclink.business.cases.GetUserNotificationsUseCase;
import com.example.doclink.business.cases.MarkNotificationAsReadUseCase;
import com.example.doclink.business.dto.NotificationsResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {

    private final GetUserNotificationsUseCase getUserNotificationsUseCase;
    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase;

    @GetMapping("/user/{userId}")
    public ResponseEntity<NotificationsResponse> getUserNotifications(@PathVariable Long userId) {
        NotificationsResponse response = getUserNotificationsUseCase.getUserNotifications(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        markNotificationAsReadUseCase.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}