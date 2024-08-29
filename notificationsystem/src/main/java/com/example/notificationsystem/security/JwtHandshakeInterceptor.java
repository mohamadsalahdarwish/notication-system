package com.example.notificationsystem.security;

import com.example.notificationsystem.consumer.NotificationKafkaConsumer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery();
        String token = null;

        if (query != null && query.contains("Authorization=Bearer ")) {
            token = query.split("Authorization=Bearer ")[1];
        }

        logger.info("Token: {}", token);

        if (token != null) {
            try {
                Claims claims = jwtUtil.extractAllClaims(token);
                if (!jwtUtil.isTokenExpired(token)) {
                    // Token is valid, proceed with the WebSocket handshake
                    attributes.put("username", claims.getSubject());
                    return true;
                }
            } catch (JwtException e) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }
        }

        response.setStatusCode(HttpStatus.FORBIDDEN);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        // No-op
    }
}

