package com.example.doclink.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletionSagaState {
    private String transactionId;
    private Long userId;
    private String userEmail;
    private SagaStatus status;
    private LocalDateTime startTime;
    private LocalDateTime lastUpdated;
    private Set<String> completedBackups = new HashSet<>();
    private Set<String> completedDeletions = new HashSet<>();
    private Set<String> completedRollbacks = new HashSet<>();
    private Set<String> failedServices = new HashSet<>();
    private String failureReason;
    private boolean timeoutExpired;

    public enum SagaStatus {
        INITIATED,
        BACKUP_IN_PROGRESS,
        BACKUP_COMPLETED,
        DELETION_IN_PROGRESS,
        DELETION_COMPLETED,
        ROLLBACK_IN_PROGRESS,
        ROLLBACK_COMPLETED,
        COMPLETED,
        FAILED
    }
}