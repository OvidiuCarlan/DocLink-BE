package com.example.appointmentservice.appointmentservice.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppointmentNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:appointment-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:appointment.created}")
    private String routingKey;

    public AppointmentNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAppointmentCreatedNotification(AppointmentNotificationMessage notification) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, notification);
    }
}