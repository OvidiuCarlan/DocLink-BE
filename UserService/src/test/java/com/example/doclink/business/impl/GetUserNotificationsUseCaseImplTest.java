package com.example.doclink.business.impl;

import com.example.doclink.business.dto.NotificationsResponse;
import com.example.doclink.persistance.NotificationRepository;
import com.example.doclink.persistance.entity.NotificationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserNotificationsUseCaseImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private GetUserNotificationsUseCaseImpl getUserNotificationsUseCase;

    private List<NotificationEntity> notifications;

    @BeforeEach
    void setUp() {
        notifications = Arrays.asList(
                NotificationEntity.builder()
                        .id(1L)
                        .userId(1L)
                        .appointmentId("appointment-1")
                        .message("Appointment scheduled")
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build(),
                NotificationEntity.builder()
                        .id(2L)
                        .userId(1L)
                        .appointmentId("appointment-2")
                        .message("Appointment reminder")
                        .createdAt(LocalDateTime.now().minusHours(1))
                        .isRead(true)
                        .build()
        );
    }

    @Test
    void getUserNotifications_ShouldReturnNotificationsResponse() {
        // Arrange
        Long userId = 1L;
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(notifications);
        when(notificationRepository.countByUserIdAndIsRead(userId, false)).thenReturn(1L);

        // Act
        NotificationsResponse response = getUserNotificationsUseCase.getUserNotifications(userId);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getNotifications().size());
        assertEquals(1L, response.getUnreadCount());

        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
        verify(notificationRepository).countByUserIdAndIsRead(userId, false);
    }

    @Test
    void getUserNotifications_WithNoNotifications_ShouldReturnEmptyResponse() {
        // Arrange
        Long userId = 1L;
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Arrays.asList());
        when(notificationRepository.countByUserIdAndIsRead(userId, false)).thenReturn(0L);

        // Act
        NotificationsResponse response = getUserNotificationsUseCase.getUserNotifications(userId);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getNotifications().size());
        assertEquals(0L, response.getUnreadCount());
    }
}