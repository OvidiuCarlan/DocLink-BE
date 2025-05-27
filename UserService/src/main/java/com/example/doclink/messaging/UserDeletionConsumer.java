package com.example.doclink.messaging;

import com.example.doclink.business.cases.DeleteUserUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserDeletionConsumer {

    private final DeleteUserUseCase deleteUserUseCase;

    // Track completion status for each user deletion
    private final ConcurrentHashMap<Long, AtomicInteger> deletionProgress = new ConcurrentHashMap<>();
    private static final int EXPECTED_SERVICES = 2; // PostService and AppointmentService

    public UserDeletionConsumer(DeleteUserUseCase deleteUserUseCase) {
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @RabbitListener(queues = "${rabbitmq.user.deletion.completion.queue:user-deletion-completion-queue}")
    public void consumeUserDeletionCompletion(UserDeletionMessage message) {
        try {
            System.out.println("UserService: Received completion message - Action: " + message.getAction() +
                    ", Service: " + message.getServiceName() + ", User: " + message.getUserId());

            if ("DELETION_COMPLETED".equals(message.getAction())) {
                Long userId = message.getUserId();
                AtomicInteger completedServices = deletionProgress.computeIfAbsent(userId, k -> new AtomicInteger(0));

                int completed = completedServices.incrementAndGet();
                System.out.println("Service " + message.getServiceName() + " completed deletion for user " + userId +
                        " (" + completed + "/" + EXPECTED_SERVICES + ")");

                if (completed >= EXPECTED_SERVICES) {
                    // All services completed, now delete the user
                    deleteUserUseCase.completeUserDeletion(userId);
                    deletionProgress.remove(userId);
                    System.out.println("User " + userId + " deletion completed successfully");
                }
            } else if ("DELETION_FAILED".equals(message.getAction())) {
                System.err.println("User deletion failed for user " + message.getUserId() +
                        " in service " + message.getServiceName() + ": " + message.getMessage());
                // Handle failure case - could retry or alert admin
                deletionProgress.remove(message.getUserId());
            }
        } catch (Exception e) {
            System.err.println("Error processing user deletion completion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}