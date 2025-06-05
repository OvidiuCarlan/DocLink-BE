package com.example.appointmentservice.appointmentservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Appointment notification configuration
    @Value("${rabbitmq.queue.name:appointment-notification-queue}")
    private String queueName;

    @Value("${rabbitmq.exchange.name:appointment-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:appointment.created}")
    private String routingKey;

    @Value("${rabbitmq.user.deletion.exchange:user-deletion-exchange}")
    private String userDeletionExchangeName;

    @Value("${rabbitmq.user.deletion.queue:appointment-service-deletion-queue}")
    private String userDeletionQueueName;

    @Value("${rabbitmq.user.deletion.routing.key:user.deletion.requested}")
    private String userDeletionRoutingKey;

    @Value("${rabbitmq.user.deletion.completion.routing.key:user.deletion.completed}")
    private String userDeletionCompletionRoutingKey;

    // New Saga configuration
    @Value("${rabbitmq.user.deletion.saga.exchange:user-deletion-saga-exchange}")
    private String sagaExchangeName;

    @Value("${rabbitmq.user.deletion.saga.queue:appointment-service-saga-queue}")
    private String sagaQueueName;

    @Value("${rabbitmq.user.deletion.saga.routing.key:user.deletion.saga}")
    private String sagaRoutingKey;

    // Appointment notification beans
    @Bean
    public Queue appointmentQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange appointmentExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding appointmentBinding() {
        return BindingBuilder.bind(appointmentQueue()).to(appointmentExchange()).with(routingKey);
    }

    @Bean
    public TopicExchange userDeletionExchange() {
        return new TopicExchange(userDeletionExchangeName);
    }

    @Bean
    public Queue userDeletionQueue() {
        return new Queue(userDeletionQueueName, true);
    }

    @Bean
    public Binding userDeletionBinding() {
        return BindingBuilder.bind(userDeletionQueue())
                .to(userDeletionExchange())
                .with(userDeletionRoutingKey);
    }

    // Saga beans
    @Bean
    public TopicExchange userDeletionSagaExchange() {
        return new TopicExchange(sagaExchangeName);
    }

    @Bean
    public Queue userDeletionSagaQueue() {
        return new Queue(sagaQueueName, true);
    }

    @Bean
    public Binding userDeletionSagaBinding() {
        return BindingBuilder.bind(userDeletionSagaQueue())
                .to(userDeletionSagaExchange())
                .with(sagaRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}