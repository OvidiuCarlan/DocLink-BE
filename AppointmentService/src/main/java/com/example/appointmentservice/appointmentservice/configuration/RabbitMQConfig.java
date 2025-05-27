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

    //appointment notification
    @Value("${rabbitmq.queue.name:appointment-notification-queue}")
    private String queueName;

    @Value("${rabbitmq.exchange.name:appointment-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:appointment.created}")
    private String routingKey;

    //delete account
    @Value("${rabbitmq.user.deletion.exchange:user-deletion-exchange}")
    private String userDeletionExchangeName;

    @Value("${rabbitmq.user.deletion.queue:appointment-service-deletion-queue}")
    private String userDeletionQueueName;

    @Value("${rabbitmq.user.deletion.routing.key:user.deletion.requested}")
    private String userDeletionRoutingKey;


    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    // delete account
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