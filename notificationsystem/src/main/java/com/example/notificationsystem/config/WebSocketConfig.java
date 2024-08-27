package com.example.notificationsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.messaging.stomp.relay.host}")
    private String rabbitMQHost;

    @Value("${spring.messaging.stomp.relay.port}")
    private int rabbitMQPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitMQUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitMQPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configures the message broker for STOMP
        config.enableStompBrokerRelay("/topic", "/queue") // Enables a relay for the STOMP broker to RabbitMQ
                .setRelayHost(rabbitMQHost) // RabbitMQ host where the broker is running
                .setRelayPort(rabbitMQPort) // Port on which RabbitMQ listens for STOMP messages
                .setClientLogin(rabbitMQUsername) // Credentials for connecting to RabbitMQ
                .setClientPasscode(rabbitMQPassword);

        // Defines the prefix for destinations that are handled by application-specific controllers
        config.setApplicationDestinationPrefixes("/app");

        // Defines the prefix for destinations targeting specific users (used for one-to-one messaging)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the WebSocket endpoint that clients will use to connect
        registry.addEndpoint("/ws") // The URL path where WebSocket clients will connect
                .setAllowedOrigins("*") // Allows connections from any origin (for development; restrict in production)
                .withSockJS() // Enables fallback options for browsers that do not support WebSocket
                .setInterceptors(new HttpSessionHandshakeInterceptor()); // Interceptor to manage authenticatio
    }
}
