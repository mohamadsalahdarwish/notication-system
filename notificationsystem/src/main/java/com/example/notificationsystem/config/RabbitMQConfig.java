package com.example.notificationsystem.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;


@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange notificationsExchange() {
        return ExchangeBuilder.directExchange("notificationsExchange")
                .durable(true)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("notificationQueue", true);
    }

    @Bean
    public Binding binding(Queue notificationQueue, Exchange notificationsExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationsExchange)
                .with("notificationRoutingKey")
                .noargs();
    }


    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareExchange(notificationsExchange());
        admin.declareQueue(notificationQueue());
        admin.declareBinding(binding(notificationQueue(), notificationsExchange()));
        return admin;
    }
}
