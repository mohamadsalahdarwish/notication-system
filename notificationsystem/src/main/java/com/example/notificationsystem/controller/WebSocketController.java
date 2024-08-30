package com.example.notificationsystem.controller;

import com.example.notificationsystem.consumer.NotificationKafkaConsumer;
import com.example.notificationsystem.entity.Notification;
import com.example.notificationsystem.entity.NotificationDto;
import com.example.notificationsystem.service.UserService;
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
    @Autowired
    private UserService userService;

    @RabbitListener(queues = "notificationQueue")
    public void handleNotification(NotificationDto notification) {
        // Extracts the userId from the notification and sends the message to the user's specific WebSocket queue
        logger.info("Received notification from RabbitMQ: {}", notification.getMessage());
        logger.info("Received notification from RabbitMQ2: {}", notification.getUserId());

        String userId = String.valueOf(notification.getUserId());
        String userName = userService.getUserById(Long.parseLong(userId)).getUsername();
        logger.info("Sending notification to user: {}", userName);
        messagingTemplate.convertAndSendToUser(userName, "/queue", notification);
    }
}
