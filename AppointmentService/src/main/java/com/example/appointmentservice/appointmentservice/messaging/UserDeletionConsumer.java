package com.example.appointmentservice.appointmentservice.messaging;

import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDeletionConsumer {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.user.deletion.exchange:user-deletion-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.user.deletion.completion.routing.key:user.deletion.completed}")
    private String completionRoutingKey;

    public UserDeletionConsumer(AppointmentRepository appointmentRepository, RabbitTemplate rabbitTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.user.deletion.queue:appointment-service-deletion-queue}")
    public void consumeUserDeletionRequest(UserDeletionMessage message) {
        System.out.println("AppointmentService: Received deletion request - Action: " + message.getAction() +
                ", User: " + message.getUserId());

        if ("DELETION_REQUESTED".equals(message.getAction())) {
            try {
                Long userId = message.getUserId();
                System.out.println("AppointmentService: Processing deletion request for user " + userId);

                // Find and delete all appointments by the user
                List<AppointmentEntity> userAppointments = appointmentRepository.findByUserId(String.valueOf(userId));
                int deletedCount = userAppointments.size();

                if (!userAppointments.isEmpty()) {
                    appointmentRepository.deleteAll(userAppointments);
                    System.out.println("AppointmentService: Deleted " + deletedCount + " appointments for user " + userId);
                } else {
                    System.out.println("AppointmentService: No appointments found for user " + userId);
                }

                // Send completion confirmation
                UserDeletionMessage completionMessage = UserDeletionMessage.builder()
                        .userId(userId)
                        .userEmail(message.getUserEmail())
                        .action("DELETION_COMPLETED")
                        .serviceName("AppointmentService")
                        .message("Deleted " + deletedCount + " appointments for user")
                        .build();

                rabbitTemplate.convertAndSend(exchangeName, completionRoutingKey, completionMessage);
                System.out.println("AppointmentService: Sent completion message for user " + userId);

            } catch (Exception e) {
                System.err.println("AppointmentService: Error deleting user data for user " + message.getUserId() + ": " + e.getMessage());
                e.printStackTrace();

                // Send failure notification
                UserDeletionMessage failureMessage = UserDeletionMessage.builder()
                        .userId(message.getUserId())
                        .userEmail(message.getUserEmail())
                        .action("DELETION_FAILED")
                        .serviceName("AppointmentService")
                        .message("Failed to delete appointments: " + e.getMessage())
                        .build();

                rabbitTemplate.convertAndSend(exchangeName, completionRoutingKey, failureMessage);
            }
        }
    }
}