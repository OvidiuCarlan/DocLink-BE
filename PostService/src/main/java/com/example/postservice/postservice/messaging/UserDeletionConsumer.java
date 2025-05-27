package com.example.postservice.postservice.messaging;

import com.example.postservice.postservice.persistance.PostRepository;
import com.example.postservice.postservice.persistance.entity.PostEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDeletionConsumer {

    private final PostRepository postRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.user.deletion.exchange:user-deletion-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.user.deletion.completion.routing.key:user.deletion.completed}")
    private String completionRoutingKey;

    public UserDeletionConsumer(PostRepository postRepository, RabbitTemplate rabbitTemplate) {
        this.postRepository = postRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.user.deletion.queue:post-service-deletion-queue}")
    public void consumeUserDeletionRequest(UserDeletionMessage message) {
        if ("DELETION_REQUESTED".equals(message.getAction())) {
            try {
                Long userId = message.getUserId();
                System.out.println("PostService: Processing deletion request for user " + userId);

                // Find and delete all posts by the user
                List<PostEntity> userPosts = postRepository.findByUserId(String.valueOf(userId));
                int deletedCount = userPosts.size();

                if (!userPosts.isEmpty()) {
                    postRepository.deleteAll(userPosts);
                    System.out.println("PostService: Deleted " + deletedCount + " posts for user " + userId);
                } else {
                    System.out.println("PostService: No posts found for user " + userId);
                }

                // Send completion confirmation
                UserDeletionMessage completionMessage = UserDeletionMessage.builder()
                        .userId(userId)
                        .userEmail(message.getUserEmail())
                        .action("DELETION_COMPLETED")
                        .serviceName("PostService")
                        .message("Deleted " + deletedCount + " posts for user")
                        .build();

                rabbitTemplate.convertAndSend(exchangeName, completionRoutingKey, completionMessage);
                System.out.println("PostService: Sent completion message for user " + userId);

            } catch (Exception e) {
                System.err.println("PostService: Error deleting user data: " + e.getMessage());
                e.printStackTrace();

                // Send failure notification
                UserDeletionMessage failureMessage = UserDeletionMessage.builder()
                        .userId(message.getUserId())
                        .userEmail(message.getUserEmail())
                        .action("DELETION_FAILED")
                        .serviceName("PostService")
                        .message("Failed to delete posts: " + e.getMessage())
                        .build();

                rabbitTemplate.convertAndSend(exchangeName, completionRoutingKey, failureMessage);
            }
        }
    }
}