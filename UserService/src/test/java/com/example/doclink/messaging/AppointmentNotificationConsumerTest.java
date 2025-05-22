package com.example.doclink.messaging;

import com.example.doclink.persistance.NotificationRepository;
import com.example.doclink.persistance.entity.NotificationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentNotificationConsumerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private AppointmentNotificationConsumer appointmentNotificationConsumer;

    private AppointmentNotificationMessage notificationMessage;

    @BeforeEach
    void setUp() {
        notificationMessage = AppointmentNotificationMessage.builder()
                .appointmentId("appointment-123")
                .userId("1")
                .date("2024-12-01")
                .time("10:00")
                .message("Appointment scheduled for 2024-12-01 at 10:00")
                .build();
    }

    @Test
    void consumeAppointmentCreatedNotification_ShouldSaveNotification() {
        // Arrange
        ArgumentCaptor<NotificationEntity> notificationCaptor = ArgumentCaptor.forClass(NotificationEntity.class);

        // Act
        appointmentNotificationConsumer.consumeAppointmentCreatedNotification(notificationMessage);

        // Assert
        verify(notificationRepository).save(notificationCaptor.capture());

        NotificationEntity savedNotification = notificationCaptor.getValue();
        assertEquals(1L, savedNotification.getUserId());
        assertEquals("appointment-123", savedNotification.getAppointmentId());
        assertEquals("Appointment scheduled for 2024-12-01 at 10:00", savedNotification.getMessage());
        assertFalse(savedNotification.isRead());
        assertNotNull(savedNotification.getCreatedAt());
    }

    @Test
    void consumeAppointmentCreatedNotification_WithInvalidUserId_ShouldHandleException() {
        // Arrange
        notificationMessage.setUserId("invalid");

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() ->
                appointmentNotificationConsumer.consumeAppointmentCreatedNotification(notificationMessage));

        verify(notificationRepository, never()).save(any());
    }
}