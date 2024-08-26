package com.example.notificationsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.stomp.host}")
    private String rabbitMQHost;

    @Value("${spring.rabbitmq.stomp.port}")
    private int rabbitMQPort;

    @Value("${spring.rabbitmq.stomp.username}")
    private String rabbitMQUsername;

    @Value("${spring.rabbitmq.stomp.password}")
    private String rabbitMQPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(rabbitMQHost)
                .setRelayPort(rabbitMQPort)
                .setClientLogin(rabbitMQUsername)
                .setClientPasscode(rabbitMQPassword);
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
