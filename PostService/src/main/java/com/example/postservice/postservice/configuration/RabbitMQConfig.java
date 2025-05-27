package com.example.postservice.postservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.user.deletion.exchange:user-deletion-exchange}")
    private String userDeletionExchangeName;

    @Value("${rabbitmq.user.deletion.queue:post-service-deletion-queue}")
    private String userDeletionQueueName;

    @Value("${rabbitmq.user.deletion.routing.key:user.deletion.requested}")
    private String userDeletionRoutingKey;

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