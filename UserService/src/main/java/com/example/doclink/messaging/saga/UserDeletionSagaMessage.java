package com.example.doclink.messaging.saga;

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
    private Map<String, Object> backupData; // For storing backup information
    private String errorReason; // For failure details

    public enum SagaAction {
        // Backup phase
        BACKUP_REQUESTED,
        BACKUP_COMPLETED,
        BACKUP_FAILED,

        // Deletion phase
        DELETION_REQUESTED,
        DELETION_COMPLETED,
        DELETION_FAILED,

        // Rollback phase
        ROLLBACK_REQUESTED,
        ROLLBACK_COMPLETED,
        ROLLBACK_FAILED,

        // Final states
        TRANSACTION_COMPLETED,
        TRANSACTION_FAILED
    }
}