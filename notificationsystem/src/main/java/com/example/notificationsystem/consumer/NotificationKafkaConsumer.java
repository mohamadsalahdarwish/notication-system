package com.example.notificationsystem.consumer;

import com.example.notificationsystem.entity.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationKafkaConsumer.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate; // RabbitMQ template to send messages

    @KafkaListener(topics = "db-notifications.public.notifications")
    public void consume(String message) {
        // Assume message contains userId and notification details
        logger.info("Received message from Kafka: {}", message);

        Notification notification = null; // Implement your parsing logic
        try {
            notification = parseMessage(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Check if user is logged in
        if (isUserLoggedIn(notification.getId())) {
            // Send the message to RabbitMQ
            sendToRabbitMQ(notification);
        }
    }

    private boolean isUserLoggedIn(Long userId) {
        // Check if the user is logged in via Redis
        return redisTemplate.opsForSet().isMember("loggedInUsers", userId.toString());
    }

    private void sendToRabbitMQ(Notification notification) {
        // Send the notification to RabbitMQ
        amqpTemplate.convertAndSend("notificationsExchange", "notificationRoutingKey", notification);
    }

    private Notification parseMessage(String message) throws JsonProcessingException {
        // Parse the Kafka message to Notification object (assumed to be JSON in this case)
        // Implement your logic here to map message to Notification object
        return new ObjectMapper().readValue(message, Notification.class);
    }
}
