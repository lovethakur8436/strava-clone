package com.fitness.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActivityEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public ActivityEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${messaging.exchange}") String exchangeName,
            @Value("${messaging.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void publishActivityCreated(ActivityCreatedEvent event) {
        // Converts the Java Object to JSON and drops it in the Exchange
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        System.out.println("[API THREAD] - Message sent to RabbitMQ for Activity: " + event.activityId());
    }
}