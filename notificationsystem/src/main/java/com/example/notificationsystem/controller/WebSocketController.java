package com.example.notificationsystem.controller;

import com.example.notificationsystem.consumer.NotificationKafkaConsumer;
import com.example.notificationsystem.entity.Notification;
import com.example.notificationsystem.entity.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "notificationQueue")
    public void handleNotification(NotificationDto notification) {
        // Extracts the userId from the notification and sends the message to the user's specific WebSocket queue
        logger.info("Received notification from RabbitMQ: {}", notification.getMessage());
        String userId = String.valueOf(notification.getUserId());
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }
}
