package com.example.notificationsystem.controller;

import com.example.notificationsystem.consumer.NotificationKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationKafkaConsumer.class);


    @MessageMapping("/send")
    @SendTo("/topic/notifications")
    public String sendNotification(String message) {
        logger.info("Received message from WebSocket: {}", message);
        return message;
    }
}

