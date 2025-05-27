package com.example.doclink.business.impl;

import com.example.doclink.business.cases.DeleteUserUseCase;
import com.example.doclink.messaging.UserDeletionMessage;
import com.example.doclink.messaging.UserDeletionPublisher;
import com.example.doclink.persistance.NotificationRepository;
import com.example.doclink.persistance.UserRepository;
import com.example.doclink.persistance.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserDeletionPublisher userDeletionPublisher;

    @Override
    public void initiateUserDeletion(Long userId) {
        // Verify user exists
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        System.out.println("UserService: Initiating deletion for user: " + userId + " (" + user.getEmail() + ")");

        // Send deletion request to all microservices
        UserDeletionMessage message = UserDeletionMessage.builder()
                .userId(userId)
                .userEmail(user.getEmail())
                .action("DELETION_REQUESTED")
                .serviceName("UserService")
                .message("User requested account deletion")
                .build();

        userDeletionPublisher.publishUserDeletionRequest(message);
        System.out.println("UserService: Published deletion request for user: " + userId);
    }

    @Override
    public void completeUserDeletion(Long userId) {
        try {
            System.out.println("UserService: Starting final deletion for user: " + userId);

            // Delete user's notifications first
            int notificationCount = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
            notificationRepository.deleteAll(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
            System.out.println("UserService: Deleted " + notificationCount + " notifications for user: " + userId);

            // Delete the user
            userRepository.deleteById(userId);
            System.out.println("UserService: Successfully deleted user: " + userId);

        } catch (Exception e) {
            System.err.println("UserService: Failed to delete user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to complete user deletion", e);
        }
    }
}