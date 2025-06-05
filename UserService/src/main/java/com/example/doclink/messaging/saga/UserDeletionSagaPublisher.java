package com.example.doclink.messaging.saga;

import com.example.doclink.messaging.saga.UserDeletionSagaMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserDeletionSagaPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.user.deletion.saga.exchange:user-deletion-saga-exchange}")
    private String sagaExchangeName;

    @Value("${rabbitmq.user.deletion.saga.routing.key:user.deletion.saga}")
    private String sagaRoutingKey;

    public UserDeletionSagaPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishSagaMessage(UserDeletionSagaMessage message) {
        rabbitTemplate.convertAndSend(sagaExchangeName, sagaRoutingKey, message);
    }
}