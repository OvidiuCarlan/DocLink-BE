package com.example.doclink.messaging;

import com.example.doclink.persistance.NotificationRepository;
import com.example.doclink.persistance.entity.NotificationEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentNotificationConsumer {

    private final NotificationRepository notificationRepository;

    public AppointmentNotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name:appointment-notification-queue}")
    public void consumeAppointmentCreatedNotification(AppointmentNotificationMessage notification) {
        try {
            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .userId(Long.valueOf(notification.getUserId()))
                    .appointmentId(notification.getAppointmentId())
                    .message(notification.getMessage())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();

            notificationRepository.save(notificationEntity);
        } catch (Exception e) {
            System.err.println("Error processing notification: " + e.getMessage());
            // In a production environment, you'd likely want to use a logger and handle the error more gracefully
        }
    }
}