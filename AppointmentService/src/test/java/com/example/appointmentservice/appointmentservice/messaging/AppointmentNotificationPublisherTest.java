package com.example.appointmentservice.appointmentservice.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentNotificationPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AppointmentNotificationPublisher appointmentNotificationPublisher;

    private AppointmentNotificationMessage notificationMessage;

    @BeforeEach
    void setUp() {
        // Set the field values using reflection
        ReflectionTestUtils.setField(appointmentNotificationPublisher, "exchangeName", "appointment-exchange");
        ReflectionTestUtils.setField(appointmentNotificationPublisher, "routingKey", "appointment.created");

        notificationMessage = AppointmentNotificationMessage.builder()
                .appointmentId("appointment-123")
                .userId("1")
                .date("2024-12-01")
                .time("10:00")
                .message("Appointment scheduled")
                .build();
    }

    @Test
    void publishAppointmentCreatedNotification_ShouldSendMessage() {
        // Act
        appointmentNotificationPublisher.publishAppointmentCreatedNotification(notificationMessage);

        // Assert
        verify(rabbitTemplate).convertAndSend("appointment-exchange", "appointment.created", notificationMessage);
    }
}