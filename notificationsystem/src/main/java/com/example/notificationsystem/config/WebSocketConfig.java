package com.example.notificationsystem.config;

import com.example.notificationsystem.security.JwtHandshakeInterceptor;
import com.example.notificationsystem.security.JwtUtil;
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

    private final JwtUtil jwtUtil;

    public WebSocketConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configures the message broker for STOMP
        config.enableStompBrokerRelay("/topic", "/queue") // Enables a relay for the STOMP broker to RabbitMQ
                .setRelayHost(rabbitMQHost) // RabbitMQ host where the broker is running
                .setRelayPort(rabbitMQPort) // Port on which RabbitMQ listens for STOMP messages
                .setClientLogin(rabbitMQUsername) // Credentials for connecting to RabbitMQ
                .setClientPasscode(rabbitMQPassword)// Credentials for connecting to RabbitMQ
                .setSystemHeartbeatReceiveInterval(10000) // Increased interval to reduce connection drops
                .setSystemHeartbeatSendInterval(10000);  // Increased interval to reduce connection drops


        // Defines the prefix for destinations that are handled by application-specific controllers
        config.setApplicationDestinationPrefixes("/app");

        // Defines the prefix for destinations targeting specific users (used for one-to-one messaging)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))  // Enable JWT validation in handshake
                .withSockJS();
    }
}
