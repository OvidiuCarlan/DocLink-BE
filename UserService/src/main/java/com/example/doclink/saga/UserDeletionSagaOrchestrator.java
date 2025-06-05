package com.example.doclink.saga;

import com.example.doclink.messaging.saga.UserDeletionSagaMessage;
import com.example.doclink.messaging.saga.UserDeletionSagaPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class UserDeletionSagaOrchestrator {

    private final UserDeletionSagaPublisher sagaPublisher;
    private final Map<String, UserDeletionSagaState> activeSagas = new ConcurrentHashMap<>();

    private static final Set<String> PARTICIPATING_SERVICES = Set.of("PostService", "AppointmentService");
    private static final long SAGA_TIMEOUT_MINUTES = 10;

    public String initiateDeletionSaga(Long userId, String userEmail) {
        String transactionId = UUID.randomUUID().toString();

        UserDeletionSagaState sagaState = UserDeletionSagaState.builder()
                .transactionId(transactionId)
                .userId(userId)
                .userEmail(userEmail)
                .status(UserDeletionSagaState.SagaStatus.INITIATED)
                .startTime(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();

        activeSagas.put(transactionId, sagaState);

        log.info("Initiated deletion saga {} for user {}", transactionId, userId);

        // Start with backup phase
        startBackupPhase(sagaState);

        return transactionId;
    }

    private void startBackupPhase(UserDeletionSagaState sagaState) {
        sagaState.setStatus(UserDeletionSagaState.SagaStatus.BACKUP_IN_PROGRESS);
        sagaState.setLastUpdated(LocalDateTime.now());

        UserDeletionSagaMessage backupMessage = UserDeletionSagaMessage.builder()
                .transactionId(sagaState.getTransactionId())
                .userId(sagaState.getUserId())
                .userEmail(sagaState.getUserEmail())
                .action(UserDeletionSagaMessage.SagaAction.BACKUP_REQUESTED)
                .serviceName("UserService")
                .message("Backup user data before deletion")
                .timestamp(LocalDateTime.now())
                .sequenceNumber(1)
                .build();

        sagaPublisher.publishSagaMessage(backupMessage);
        log.info("Started backup phase for saga {}", sagaState.getTransactionId());
    }

    public void handleSagaMessage(UserDeletionSagaMessage message) {
        UserDeletionSagaState sagaState = activeSagas.get(message.getTransactionId());

        if (sagaState == null) {
            log.warn("Received message for unknown saga: {}", message.getTransactionId());
            return;
        }

        sagaState.setLastUpdated(LocalDateTime.now());

        switch (message.getAction()) {
            case BACKUP_COMPLETED:
                handleBackupCompleted(sagaState, message);
                break;
            case BACKUP_FAILED:
                handleBackupFailed(sagaState, message);
                break;
            case DELETION_COMPLETED:
                handleDeletionCompleted(sagaState, message);
                break;
            case DELETION_FAILED:
                handleDeletionFailed(sagaState, message);
                break;
            case ROLLBACK_COMPLETED:
                handleRollbackCompleted(sagaState, message);
                break;
            case ROLLBACK_FAILED:
                handleRollbackFailed(sagaState, message);
                break;
            default:
                log.warn("Unhandled saga action: {} for transaction {}",
                        message.getAction(), message.getTransactionId());
        }
    }

    private void handleBackupCompleted(UserDeletionSagaState sagaState, UserDeletionSagaMessage message) {
        sagaState.getCompletedBackups().add(message.getServiceName());

        log.info("Backup completed by {} for saga {}",
                message.getServiceName(), sagaState.getTransactionId());

        if (allServicesCompletedBackup(sagaState)) {
            sagaState.setStatus(UserDeletionSagaState.SagaStatus.BACKUP_COMPLETED);
            startDeletionPhase(sagaState);
        }
    }

    private void handleBackupFailed(UserDeletionSagaState sagaState, UserDeletionSagaMessage message) {
        log.error("Backup failed by {} for saga {}: {}",
                message.getServiceName(), sagaState.getTransactionId(), message.getErrorReason());

        sagaState.getFailedServices().add(message.getServiceName());
        sagaState.setFailureReason(message.getErrorReason());
        failSaga(sagaState, "Backup phase failed");
    }

    private void startDeletionPhase(UserDeletionSagaState sagaState) {
        sagaState.setStatus(UserDeletionSagaState.SagaStatus.DELETION_IN_PROGRESS);

        UserDeletionSagaMessage deletionMessage = UserDeletionSagaMessage.builder()
                .transactionId(sagaState.getTransactionId())
                .userId(sagaState.getUserId())
                .userEmail(sagaState.getUserEmail())
                .action(UserDeletionSagaMessage.SagaAction.DELETION_REQUESTED)
                .serviceName("UserService")
                .message("Delete user data")
                .timestamp(LocalDateTime.now())
                .sequenceNumber(2)
                .build();

        sagaPublisher.publishSagaMessage(deletionMessage);
        log.info("Started deletion phase for saga {}", sagaState.getTransactionId());
    }

    private void handleDeletionCompleted(UserDeletionSagaState sagaState, UserDeletionSagaMessage message) {
        sagaState.getCompletedDeletions().add(message.getServiceName());

        log.info("Deletion completed by {} for saga {}",
                message.getServiceName(), sagaState.getTransactionId());

        if (allServicesCompletedDeletion(sagaState)) {
            completeSaga(sagaState);
        }
    }

    private void handleDeletionFailed(UserDeletionSagaState sagaState, UserDeletionSagaMessage message) {
        log.error("Deletion failed by {} for saga {}: {}",
                message.getServiceName(), sagaState.getTransactionId(), message.getErrorReason());

        sagaState.getFailedServices().add(message.getServiceName());
        sagaState.setFailureReason(message.getErrorReason());
        startRollbackPhase(sagaState);
    }

    private void startRollbackPhase(UserDeletionSagaState sagaState) {
        sagaState.setStatus(UserDeletionSagaState.SagaStatus.ROLLBACK_IN_PROGRESS);

        UserDeletionSagaMessage rollbackMessage = UserDeletionSagaMessage.builder()
                .transactionId(sagaState.getTransactionId())
                .userId(sagaState.getUserId())
                .userEmail(sagaState.getUserEmail())
                .action(UserDeletionSagaMessage.SagaAction.ROLLBACK_REQUESTED)
                .serviceName("UserService")
                .message("Rollback deletion due to failure")
                .timestamp(LocalDateTime.now())
                .sequenceNumber(3)
                .build();

        sagaPublisher.publishSagaMessage(rollbackMessage);
        log.info("Started rollback phase for saga {}", sagaState.getTransactionId());
    }

    private void handleRollbackCompleted(UserDeletionSagaState sagaState, UserDeletionSagaMessage message) {
        sagaState.getCompletedRollbacks().add(message.getServiceName());

        log.info("Rollback completed by {} for saga {}",
                message.getServiceName(), sagaState.getTransactionId());

        if (allServicesCompletedRollback(sagaState)) {
            sagaState.setStatus(UserDeletionSagaState.SagaStatus.ROLLBACK_COMPLETED);
            failSaga(sagaState, "Transaction rolled back successfully");
        }
    }

    private void handleRollbackFailed(UserDeletionSagaState sagaState, UserDeletionSagaMessage message) {
        log.error("Rollback failed by {} for saga {}: {}",
                message.getServiceName(), sagaState.getTransactionId(), message.getErrorReason());

        // This is a critical error - rollback itself failed
        failSaga(sagaState, "Rollback failed: " + message.getErrorReason());
    }

    private void completeSaga(UserDeletionSagaState sagaState) {
        sagaState.setStatus(UserDeletionSagaState.SagaStatus.COMPLETED);

        // Delete user data in UserService as final step
        deleteUserData(sagaState);

        UserDeletionSagaMessage completionMessage = UserDeletionSagaMessage.builder()
                .transactionId(sagaState.getTransactionId())
                .userId(sagaState.getUserId())
                .userEmail(sagaState.getUserEmail())
                .action(UserDeletionSagaMessage.SagaAction.TRANSACTION_COMPLETED)
                .serviceName("UserService")
                .message("User deletion completed successfully")
                .timestamp(LocalDateTime.now())
                .build();

        sagaPublisher.publishSagaMessage(completionMessage);

        log.info("Saga {} completed successfully for user {}",
                sagaState.getTransactionId(), sagaState.getUserId());

        activeSagas.remove(sagaState.getTransactionId());
    }

    private void failSaga(UserDeletionSagaState sagaState, String reason) {
        sagaState.setStatus(UserDeletionSagaState.SagaStatus.FAILED);
        sagaState.setFailureReason(reason);

        UserDeletionSagaMessage failureMessage = UserDeletionSagaMessage.builder()
                .transactionId(sagaState.getTransactionId())
                .userId(sagaState.getUserId())
                .userEmail(sagaState.getUserEmail())
                .action(UserDeletionSagaMessage.SagaAction.TRANSACTION_FAILED)
                .serviceName("UserService")
                .message("User deletion failed: " + reason)
                .errorReason(reason)
                .timestamp(LocalDateTime.now())
                .build();

        sagaPublisher.publishSagaMessage(failureMessage);

        log.error("Saga {} failed for user {}: {}",
                sagaState.getTransactionId(), sagaState.getUserId(), reason);

        activeSagas.remove(sagaState.getTransactionId());
    }

    private boolean allServicesCompletedBackup(UserDeletionSagaState sagaState) {
        return sagaState.getCompletedBackups().containsAll(PARTICIPATING_SERVICES);
    }

    private boolean allServicesCompletedDeletion(UserDeletionSagaState sagaState) {
        return sagaState.getCompletedDeletions().containsAll(PARTICIPATING_SERVICES);
    }

    private boolean allServicesCompletedRollback(UserDeletionSagaState sagaState) {
        return sagaState.getCompletedRollbacks().containsAll(PARTICIPATING_SERVICES);
    }

    private void deleteUserData(UserDeletionSagaState sagaState) {
        // Implementation for deleting user and notifications in UserService
        // This should be called only when all other services have successfully deleted their data
        log.info("Deleting user data for user {} in transaction {}",
                sagaState.getUserId(), sagaState.getTransactionId());
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void handleTimeouts() {
        LocalDateTime now = LocalDateTime.now();

        List<UserDeletionSagaState> timedOutSagas = activeSagas.values().stream()
                .filter(saga -> ChronoUnit.MINUTES.between(saga.getStartTime(), now) > SAGA_TIMEOUT_MINUTES)
                .filter(saga -> !saga.isTimeoutExpired())
                .toList();

        for (UserDeletionSagaState saga : timedOutSagas) {
            saga.setTimeoutExpired(true);
            log.warn("Saga {} timed out after {} minutes", saga.getTransactionId(), SAGA_TIMEOUT_MINUTES);

            if (saga.getStatus() != UserDeletionSagaState.SagaStatus.ROLLBACK_IN_PROGRESS &&
                    saga.getStatus() != UserDeletionSagaState.SagaStatus.ROLLBACK_COMPLETED) {
                startRollbackPhase(saga);
            }
        }
    }
}