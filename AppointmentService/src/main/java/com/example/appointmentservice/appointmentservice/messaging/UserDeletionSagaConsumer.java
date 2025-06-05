package com.example.appointmentservice.appointmentservice.messaging;

import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserDeletionSagaConsumer {

    private final AppointmentRepository appointmentRepository;
    private final RabbitTemplate rabbitTemplate;

    // Store backup data per transaction
    private final Map<String, List<AppointmentEntity>> backupStorage = new ConcurrentHashMap<>();

    @Value("${rabbitmq.user.deletion.saga.exchange:user-deletion-saga-exchange}")
    private String sagaExchangeName;

    @Value("${rabbitmq.user.deletion.saga.routing.key:user.deletion.saga}")
    private String sagaRoutingKey;

    public UserDeletionSagaConsumer(AppointmentRepository appointmentRepository, RabbitTemplate rabbitTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.user.deletion.saga.queue:appointment-service-saga-queue}")
    public void handleSagaMessage(UserDeletionSagaMessage message) {
        System.out.println("AppointmentService: Received saga message - Action: " + message.getAction() +
                ", Transaction: " + message.getTransactionId() + ", User: " + message.getUserId());

        switch (message.getAction()) {
            case BACKUP_REQUESTED:
                handleBackupRequest(message);
                break;
            case DELETION_REQUESTED:
                handleDeletionRequest(message);
                break;
            case ROLLBACK_REQUESTED:
                handleRollbackRequest(message);
                break;
            default:
                System.out.println("AppointmentService: Ignoring message with action " + message.getAction());
        }
    }

    private void handleBackupRequest(UserDeletionSagaMessage message) {
        try {
            String transactionId = message.getTransactionId();
            Long userId = message.getUserId();

            System.out.println("AppointmentService: Starting backup for user " + userId + " in transaction " + transactionId);

            // Find all appointments by the user
            List<AppointmentEntity> userAppointments = appointmentRepository.findByUserId(String.valueOf(userId));

            // Store backup
            backupStorage.put(transactionId, userAppointments);

            System.out.println("AppointmentService: Backed up " + userAppointments.size() + " appointments for user " + userId);

            // Send backup completion message
            UserDeletionSagaMessage backupCompleted = UserDeletionSagaMessage.builder()
                    .transactionId(transactionId)
                    .userId(userId)
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.BACKUP_COMPLETED)
                    .serviceName("AppointmentService")
                    .message("Backed up " + userAppointments.size() + " appointments")
                    .timestamp(LocalDateTime.now())
                    .backupData(Map.of("appointmentCount", userAppointments.size()))
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, backupCompleted);

        } catch (Exception e) {
            System.err.println("AppointmentService: Backup failed for transaction " + message.getTransactionId() + ": " + e.getMessage());

            UserDeletionSagaMessage backupFailed = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.BACKUP_FAILED)
                    .serviceName("AppointmentService")
                    .message("Backup failed")
                    .errorReason(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, backupFailed);
        }
    }

    private void handleDeletionRequest(UserDeletionSagaMessage message) {
        try {
            String transactionId = message.getTransactionId();
            Long userId = message.getUserId();

            System.out.println("AppointmentService: Starting deletion for user " + userId + " in transaction " + transactionId);

            // Get backed up appointments for verification
            List<AppointmentEntity> backedUpAppointments = backupStorage.get(transactionId);
            if (backedUpAppointments == null) {
                throw new RuntimeException("No backup found for transaction " + transactionId);
            }

            // Delete appointments
            List<AppointmentEntity> currentAppointments = appointmentRepository.findByUserId(String.valueOf(userId));
            if (!currentAppointments.isEmpty()) {
                appointmentRepository.deleteAll(currentAppointments);
            }

            System.out.println("AppointmentService: Deleted " + currentAppointments.size() + " appointments for user " + userId);

            // Send deletion completion message
            UserDeletionSagaMessage deletionCompleted = UserDeletionSagaMessage.builder()
                    .transactionId(transactionId)
                    .userId(userId)
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.DELETION_COMPLETED)
                    .serviceName("AppointmentService")
                    .message("Deleted " + currentAppointments.size() + " appointments")
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, deletionCompleted);

        } catch (Exception e) {
            System.err.println("AppointmentService: Deletion failed for transaction " + message.getTransactionId() + ": " + e.getMessage());

            UserDeletionSagaMessage deletionFailed = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.DELETION_FAILED)
                    .serviceName("AppointmentService")
                    .message("Deletion failed")
                    .errorReason(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, deletionFailed);
        }
    }

    private void handleRollbackRequest(UserDeletionSagaMessage message) {
        try {
            String transactionId = message.getTransactionId();
            Long userId = message.getUserId();

            System.out.println("AppointmentService: Starting rollback for user " + userId + " in transaction " + transactionId);

            // Restore from backup
            List<AppointmentEntity> backedUpAppointments = backupStorage.get(transactionId);
            if (backedUpAppointments != null && !backedUpAppointments.isEmpty()) {
                appointmentRepository.saveAll(backedUpAppointments);
                System.out.println("AppointmentService: Restored " + backedUpAppointments.size() + " appointments for user " + userId);
            }

            // Clean up backup
            backupStorage.remove(transactionId);

            // Send rollback completion message
            UserDeletionSagaMessage rollbackCompleted = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.ROLLBACK_COMPLETED)
                    .serviceName("AppointmentService")
                    .message("Rollback completed - restored " + (backedUpAppointments != null ? backedUpAppointments.size() : 0) + " appointments")
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, rollbackCompleted);

        } catch (Exception e) {
            System.err.println("AppointmentService: Rollback failed for transaction " + message.getTransactionId() + ": " + e.getMessage());

            UserDeletionSagaMessage rollbackFailed = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.ROLLBACK_FAILED)
                    .serviceName("AppointmentService")
                    .message("Rollback failed")
                    .errorReason(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, rollbackFailed);
        }
    }
}