package com.fitness.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${messaging.exchange}")
    private String exchangeName;

    @Value("${messaging.queue}")
    private String queueName;

    @Value("${messaging.map-queue}")
    private String mapQueueName;

    @Value("${messaging.routing-key}")
    private String routingKey;

    // 1. Define the Queue (Durable = true means messages survive a broker restart)
    @Bean
    public Queue notificationQueue() {
        return new Queue(queueName, true);
    }

    // 2. Define the Exchange (The router that receives messages and pushes to
    // queues)
    @Bean
    public TopicExchange activityExchange() {
        return new TopicExchange(exchangeName);
    }

    // 3. Bind the Queue to the Exchange using the Routing Key
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange activityExchange) {
        return BindingBuilder.bind(notificationQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    public Queue mapQueue() {
        return new Queue(mapQueueName, true);
    }

    @Bean
    public Binding mapBinding(Queue mapQueue, TopicExchange activityExchange) {
        return BindingBuilder.bind(mapQueue).to(activityExchange).with(routingKey);
    }

    // 4. Force RabbitMQ to use human-readable JSON instead of binary bytecode
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}