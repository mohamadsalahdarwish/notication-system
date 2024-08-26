package com.example.notificationsystem.controller;

import com.example.notificationsystem.entity.Notification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "notificationQueue")
    public void handleNotification(Notification notification) {
        String userId = notification.getId().toString();
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }
}
