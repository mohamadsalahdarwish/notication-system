package com.example.notificationsystem.controller;

import com.example.notificationsystem.dto.request.UserRegistrationRequest;
import com.example.notificationsystem.entity.NotificationDto;
import com.example.notificationsystem.entity.TempNotification;
import com.example.notificationsystem.entity.User;
import com.example.notificationsystem.model.AuthenticationRequest;
import com.example.notificationsystem.model.AuthenticationResponse;
import com.example.notificationsystem.repository.TempNotificationRepository;
import com.example.notificationsystem.security.JwtUtil;
import com.example.notificationsystem.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TempNotificationRepository tempNotificationRepository;

    @Autowired
    public JwtAuthenticationController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtTokenUtil,
            CustomUserDetailsService userDetailsService,
            RedisTemplate<String, Object> redisTemplate,
            PasswordEncoder passwordEncoder,
            TempNotificationRepository tempNotificationRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.tempNotificationRepository = tempNotificationRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        if (userDetailsService.userExists(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userDetailsService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        authenticateUser(request.getUsername(), request.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        redisTemplate.opsForSet().add("loggedInUsers", userDetails.getUsername());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @GetMapping("/notifications/{username}")
    public ResponseEntity<List<NotificationDto>> getTempNotifications(@PathVariable String username) {
        List<NotificationDto> notifications = fetchAndDeleteTempNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String username = extractUsernameFromRequest(request);
        if (username != null) {
            redisTemplate.opsForSet().remove("loggedInUsers", username);
            request.getSession().invalidate();
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private void authenticateUser(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }
    }

    private String extractUsernameFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            return jwtTokenUtil.extractUsername(jwt);
        }
        return null;
    }
    @Transactional
    public List<NotificationDto> fetchAndDeleteTempNotifications(String username) {
        List<TempNotification> tempNotifications = tempNotificationRepository.findByUsername(username);
        List<NotificationDto> notificationDtos = tempNotifications.stream()
                .map(this::toNotificationDto)
                .collect(Collectors.toList());
        tempNotificationRepository.deleteAll(tempNotifications);
        return notificationDtos;
    }

    private NotificationDto toNotificationDto(TempNotification tempNotification) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(tempNotification.getUserId());
        notificationDto.setMessage(tempNotification.getMessage());
        return notificationDto;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
