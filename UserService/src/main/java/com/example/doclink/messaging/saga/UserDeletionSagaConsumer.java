package com.example.doclink.messaging.saga;

import com.example.doclink.saga.UserDeletionSagaOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeletionSagaConsumer {

    private final UserDeletionSagaOrchestrator sagaOrchestrator;

    public UserDeletionSagaConsumer(UserDeletionSagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @RabbitListener(queues = "${rabbitmq.user.deletion.saga.orchestrator.queue:user-deletion-saga-orchestrator-queue}")
    public void handleSagaMessage(UserDeletionSagaMessage message) {
        sagaOrchestrator.handleSagaMessage(message);
    }
}