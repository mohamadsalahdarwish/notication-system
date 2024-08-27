package com.example.notificationsystem.consumer;

import com.example.notificationsystem.entity.Notification;
import com.example.notificationsystem.entity.NotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "db-notifications.public.notifications")
    public void consume(String message) throws JsonProcessingException {
        // Assume message contains userId and notification details
        logger.info("Received message from Kafka: {}", message);

        JsonNode root = objectMapper.readTree(message);

        JsonNode afterNode = root.path("payload").path("after");

        NotificationDto notificationDto = null;
        if (!afterNode.isMissingNode()) {
            notificationDto = objectMapper.treeToValue(afterNode, NotificationDto.class);
            logger.info("Received Notification: {}" , notificationDto.getMessage());
        }


        // Check if user is logged in
        if (true){///isUserLoggedIn(notification.getId())) {
            // Send the message to RabbitMQ
            sendToRabbitMQ(notificationDto);
        }
    }

    private boolean isUserLoggedIn(Long userId) {
        // Check if the user is logged in via Redis
        return redisTemplate.opsForSet().isMember("loggedInUsers", userId.toString());
    }


    public void sendToRabbitMQ(NotificationDto notification) {
        // Converts and sends the NotificationDto to the "notificationsExchange" with the routing key "notificationRoutingKey"
        rabbitTemplate.convertAndSend("notificationsExchange", "notificationRoutingKey", notification);
    }

    private Notification parseMessage(String message) throws JsonProcessingException {
        // Parse the Kafka message to Notification object (assumed to be JSON in this case)
        // Implement your logic here to map message to Notification object
        return new ObjectMapper().readValue(message, Notification.class);
    }
}
