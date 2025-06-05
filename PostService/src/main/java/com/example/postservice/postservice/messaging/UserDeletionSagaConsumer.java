package com.example.postservice.postservice.messaging;

import com.example.postservice.postservice.persistance.PostRepository;
import com.example.postservice.postservice.persistance.entity.PostEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserDeletionSagaConsumer {

    private final PostRepository postRepository;
    private final RabbitTemplate rabbitTemplate;

    // Store backup data per transaction
    private final Map<String, List<PostEntity>> backupStorage = new ConcurrentHashMap<>();

    @Value("${rabbitmq.user.deletion.saga.exchange:user-deletion-saga-exchange}")
    private String sagaExchangeName;

    @Value("${rabbitmq.user.deletion.saga.routing.key:user.deletion.saga}")
    private String sagaRoutingKey;

    public UserDeletionSagaConsumer(PostRepository postRepository, RabbitTemplate rabbitTemplate) {
        this.postRepository = postRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.user.deletion.saga.queue:post-service-saga-queue}")
    public void handleSagaMessage(UserDeletionSagaMessage message) {
        System.out.println("PostService: Received saga message - Action: " + message.getAction() +
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
                System.out.println("PostService: Ignoring message with action " + message.getAction());
        }
    }

    private void handleBackupRequest(UserDeletionSagaMessage message) {
        try {
            String transactionId = message.getTransactionId();
            Long userId = message.getUserId();

            System.out.println("PostService: Starting backup for user " + userId + " in transaction " + transactionId);

            // Find all posts by the user
            List<PostEntity> userPosts = postRepository.findByUserId(String.valueOf(userId));

            // Store backup
            backupStorage.put(transactionId, userPosts);

            System.out.println("PostService: Backed up " + userPosts.size() + " posts for user " + userId);

            // Send backup completion message
            UserDeletionSagaMessage backupCompleted = UserDeletionSagaMessage.builder()
                    .transactionId(transactionId)
                    .userId(userId)
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.BACKUP_COMPLETED)
                    .serviceName("PostService")
                    .message("Backed up " + userPosts.size() + " posts")
                    .timestamp(LocalDateTime.now())
                    .backupData(Map.of("postCount", userPosts.size()))
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, backupCompleted);

        } catch (Exception e) {
            System.err.println("PostService: Backup failed for transaction " + message.getTransactionId() + ": " + e.getMessage());

            UserDeletionSagaMessage backupFailed = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.BACKUP_FAILED)
                    .serviceName("PostService")
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

            System.out.println("PostService: Starting deletion for user " + userId + " in transaction " + transactionId);

            // Get backed up posts for verification
            List<PostEntity> backedUpPosts = backupStorage.get(transactionId);
            if (backedUpPosts == null) {
                throw new RuntimeException("No backup found for transaction " + transactionId);
            }

            // Delete posts
            List<PostEntity> currentPosts = postRepository.findByUserId(String.valueOf(userId));
            if (!currentPosts.isEmpty()) {
                postRepository.deleteAll(currentPosts);
            }

            System.out.println("PostService: Deleted " + currentPosts.size() + " posts for user " + userId);

            // Send deletion completion message
            UserDeletionSagaMessage deletionCompleted = UserDeletionSagaMessage.builder()
                    .transactionId(transactionId)
                    .userId(userId)
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.DELETION_COMPLETED)
                    .serviceName("PostService")
                    .message("Deleted " + currentPosts.size() + " posts")
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, deletionCompleted);

        } catch (Exception e) {
            System.err.println("PostService: Deletion failed for transaction " + message.getTransactionId() + ": " + e.getMessage());

            UserDeletionSagaMessage deletionFailed = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.DELETION_FAILED)
                    .serviceName("PostService")
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

            System.out.println("PostService: Starting rollback for user " + userId + " in transaction " + transactionId);

            // Restore from backup
            List<PostEntity> backedUpPosts = backupStorage.get(transactionId);
            if (backedUpPosts != null && !backedUpPosts.isEmpty()) {
                postRepository.saveAll(backedUpPosts);
                System.out.println("PostService: Restored " + backedUpPosts.size() + " posts for user " + userId);
            }

            // Clean up backup
            backupStorage.remove(transactionId);

            // Send rollback completion message
            UserDeletionSagaMessage rollbackCompleted = UserDeletionSagaMessage.builder()
                    .transactionId(transactionId)
                    .userId(userId)
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.ROLLBACK_COMPLETED)
                    .serviceName("PostService")
                    .message("Rollback completed - restored " + (backedUpPosts != null ? backedUpPosts.size() : 0) + " posts")
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, rollbackCompleted);

        } catch (Exception e) {
            System.err.println("PostService: Rollback failed for transaction " + message.getTransactionId() + ": " + e.getMessage());

            UserDeletionSagaMessage rollbackFailed = UserDeletionSagaMessage.builder()
                    .transactionId(message.getTransactionId())
                    .userId(message.getUserId())
                    .userEmail(message.getUserEmail())
                    .action(UserDeletionSagaMessage.SagaAction.ROLLBACK_FAILED)
                    .serviceName("PostService")
                    .message("Rollback failed")
                    .errorReason(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, rollbackFailed);
        }
    }
}