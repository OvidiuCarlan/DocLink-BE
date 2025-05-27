package com.example.doclink.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserDeletionPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.user.deletion.exchange:user-deletion-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.user.deletion.routing.key:user.deletion.requested}")
    private String deletionRequestedRoutingKey;

    public UserDeletionPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserDeletionRequest(UserDeletionMessage message) {
        rabbitTemplate.convertAndSend(exchangeName, deletionRequestedRoutingKey, message);
    }
}