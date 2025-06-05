package com.example.appointmentservice.appointmentservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletionSagaMessage implements Serializable {
    private String transactionId;
    private Long userId;
    private String userEmail;
    private SagaAction action;
    private String serviceName;
    private String message;
    private LocalDateTime timestamp;
    private int sequenceNumber;
    private Map<String, Object> backupData;
    private String errorReason;

    public enum SagaAction {
        BACKUP_REQUESTED,
        BACKUP_COMPLETED,
        BACKUP_FAILED,
        DELETION_REQUESTED,
        DELETION_COMPLETED,
        DELETION_FAILED,
        ROLLBACK_REQUESTED,
        ROLLBACK_COMPLETED,
        ROLLBACK_FAILED,
        TRANSACTION_COMPLETED,
        TRANSACTION_FAILED
    }
}