# **Building a Real-Time Notification System with Spring Boot and Angular**

## **Introduction**

In this tutorial, we'll build a real-time notification system using Spring Boot for the backend and Angular for the frontend. The backend will handle user authentication and authorization, while Kafka will manage real-time messaging. Redis will handle session management, and Docker will orchestrate the entire stack. We'll be using Kafka Connect  to capture database changes and stream them into Kafka topics.

## **Technologies Used**

- **Spring Boot**: Backend framework for building RESTful services and business logic.
- **Angular**: Frontend framework for building dynamic web applications.
- **Kafka**: Distributed messaging system used as a message broker for real-time notifications.
- **Redis**: In-memory data store used for session management.
- **Docker**: Containerization tool for packaging and deploying the application.
- **PostgreSQL**: Relational database for storing user and notification data.
- **WebSocket**: Protocol for real-time, bidirectional communication between client and server.

## **Architecture Overview**

### **High-Level Components**

1. **Spring Boot Backend**:
   - Manages user authentication and authorization using JWT (JSON Web Tokens).
   - Stores session information in Redis.
   - Sends real-time notifications via WebSocket and Kafka.
   - Stores data in a PostgreSQL database.

2. **Angular Frontend**:
   - Provides a user interface for login, notifications, and other components.
   - Communicates with the backend via HTTP and WebSocket.

3. **Supporting Services**:
   - **Kafka**: Manages real-time messaging.
   - **Redis**: Manages user sessions.
   - **PostgreSQL**: Stores user and notification data.
   - **Docker**: Orchestrates the application stack.

---

## **Folder Structure and Organization**

### **1. Spring Boot Backend Folder Structure**

```plaintext
/src
 ├── /main
 │   ├── /java
 │   │   └── /com
 │   │       └── /example
 │   │           └── /notificationsystem
 │   │               ├── /controller        # REST and WebSocket controllers
 │   │               │   ├── JwtAuthenticationController.java
 │   │               │   └── NotificationController.java
 │   │               ├── /entity            # JPA entities (data model)
 │   │               │   ├── User.java
 │   │               │   ├── Role.java
 │   │               │   └── Notification.java
 │   │               ├── /repository        # JPA repositories (data access layer)
 │   │               │   ├── UserRepository.java
 │   │               │   ├── RoleRepository.java
 │   │               │   └── NotificationRepository.java
 │   │               ├── /service           # Business logic layer
 │   │               │   ├── UserService.java
 │   │               │   ├── NotificationService.java
 │   │               │   └── CustomUserDetailsService.java
 │   │               ├── /security          # Security configurations and utilities
 │   │               │   ├── JwtUtil.java
 │   │               │   ├── JwtRequestFilter.java
 │   │               │   └── SecurityConfig.java
 │   │               ├── /config            # Application configurations (WebSocket, Kafka, etc.)
 │   │               │   ├── WebSocketConfig.java
 │   │               │   └── KafkaConfig.java
 │   │               ├── /exception         # Custom exception handling
 │   │               │   └── CustomExceptionHandler.java
 │   │               ├── /model             # DTOs, request/response models
 │   │               │   ├── AuthenticationRequest.java
 │   │               │   ├── AuthenticationResponse.java
 │   │               │   └── NotificationPayload.java
 │   │               ├── /util              # Utility classes (e.g., JWT utilities)
 │   │               │   └── KafkaMessageConverter.java
 │   │               └── Application.java   # Main Spring Boot application class
 │   ├── /resources
 │   │   ├── /static                        # Static resources (CSS, JavaScript, images)
 │   │   ├── /templates                     # Server-side templates (Thymeleaf, etc.)
 │   │   ├── /db                            # Database migration scripts (Liquibase, Flyway)
 │   │   │   └── V1__Initial_Setup.sql
 │   │   ├── application.properties         # Application configuration file
 │   │   └── application.yml                # Alternative YAML configuration
 ├── /test                                  # Unit and integration tests
 │   └── /java
 │       └── /com
 │           └── /example
 │               └── /notificationsystem
 │                   ├── /controller        # Controller tests
 │                   ├── /service           # Service tests
 │                   └── /repository        # Repository tests
 └── /docker
     ├── Dockerfile                         # Dockerfile for Spring Boot application
     └── docker-compose.yml                 # Docker Compose for entire stack
```

### **2. Angular Frontend Folder Structure**

```plaintext
/src
 ├── /app
 │   ├── /components
 │   │   ├── /login                         # Login component
 │   │   │   ├── login.component.ts         # Login component logic
 │   │   │   ├── login.component.html       # Login component template
 │   │   │   └── login.component.css        # Login component styles
 │   │   ├── /notifications                 # Notifications component
 │   │   │   ├── notifications.component.ts # Notifications component logic
 │   │   │   ├── notifications.component.html # Notifications template
 │   │   │   └── notifications.component.css # Notifications styles
 │   │   └── /other-components              # Other components
 │   ├── /services                          # Angular services
 │   │   ├── auth.service.ts                # Authentication service
 │   │   ├── websocket.service.ts           # WebSocket service
 │   │   └── other-services.ts              # Other services
 │   ├── /guards                            # Route guards
 │   │   ├── auth.guard.ts                  # Route guard for authentication
 │   └── /models                            # Data models
 │       ├── user.model.ts                  # User model
 │       └── notification.model.ts          # Notification model
 ├── /assets                                # Static assets (images, fonts, etc.)
 ├── /environments
 │   ├── environment.ts                     # Development environment settings
 │   └── environment.prod.ts                # Production environment settings
 ├── /styles                                # Global styles
 │   └── styles.css                         # Global stylesheet
 ├── index.html                             # Main HTML file
 ├── main.ts                                # Entry point for the Angular application
 ├── app.module.ts                          # Root module of the Angular application
 └── app-routing.module.ts                  # Routing module for Angular application
```

### **3. Docker and DevOps Folder Structure**

```plaintext
/docker
 ├── Dockerfile                             # Dockerfile for Spring Boot application
 └── docker-compose.yml                     # Docker Compose for entire stack
```

---

## **Step-by-Step Implementation**

### **1. Spring Boot Backend Implementation**

#### **1.1. Create the Spring Boot Project**

- **Setup**: Use [Spring Initializr](https://start.spring.io/) to generate the project.
  - **Dependencies**: Add Spring Web, Spring Data JPA, Spring Security, Spring Kafka, Spring WebSocket, Spring Session Data Redis, and PostgreSQL Driver.
  - **Structure**: Follow the folder structure mentioned above for organizing the project.

#### **1.2. Define Entity Classes**

- **User.java**:
  - Represents the `users` table in the database.
  - Contains fields like `id`, `username`, `password`, `enabled`, and `roles`.
  - Example:
    ```java
    @Entity
    @Table(name = "users")
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String username;
        private String password;
        private boolean enabled;
        @ManyToMany(fetch = FetchType.EAGER)
        private Set<Role> roles;
        // Getters and Setters
    }
    ```

- **Role.java**:
  - Represents the `roles` table.
  - Fields: `id`, `name`.
  - Example:
    ```java
    @Entity
    @Table(name = "roles")
    public class Role {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        // Getters and Setters
    }
    ```

- **Notification.java**:
  - Represents the `notifications` table.
  - Fields: `id`, `userId`, `message`.
  - Example:
    ```java
    @Entity
    @Table(name = "notifications")
    public class Notification {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long

 id;
        private Long userId;
        private String message;
        // Getters and Setters
    }
    ```

#### **1.3. Create JPA Repositories**

Repositories provide an abstraction layer between the data access layer and business logic. They make data retrieval, storage, and querying easier by using JPA.

- **UserRepository.java**:
  - Interface extending `JpaRepository<User, Long>`.
  - Provides methods like `findByUsername`.
  - Example:
    ```java
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);
    }
    ```

- **RoleRepository.java**:
  - Interface extending `JpaRepository<Role, Long>`.

- **NotificationRepository.java**:
  - Interface extending `JpaRepository<Notification, Long>`.
  - Example:
    ```java
    @Repository
    public interface NotificationRepository extends JpaRepository<Notification, Long> {
        List<Notification> findByUserId(Long userId);
    }
    ```

#### **1.4. Implement Services**

Services encapsulate business logic and are used by controllers to interact with repositories.

- **UserService.java**:
  - Handles user-related operations such as registration, role assignment, and fetching user details.
  - Example:
    ```java
    @Service
    public class UserService {
        @Autowired
        private UserRepository userRepository;

        public User createUser(User user) {
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            return userRepository.save(user);
        }
        // Other methods...
    }
    ```

- **NotificationService.java**:
  - Manages notifications, including sending and retrieving notifications for users.

- **CustomUserDetailsService.java**:
  - Implements `UserDetailsService` from Spring Security to load user details for authentication.
  - Example:
    ```java
    @Service
    public class CustomUserDetailsService implements UserDetailsService {
        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList())
            );
        }
    }
    ```

#### **1.5. Set Up JWT Security**

JWT (JSON Web Token) is a compact and self-contained way to securely transmit information between parties as a JSON object.

- **JwtUtil.java**:
  - Utility class for generating, validating, and parsing JWT tokens.
  - Example:
    ```java
    package com.example.notificationsystem.security;

    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Service;

    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.function.Function;

    @Service
    public class JwtUtil {

        private String secret = "your_jwt_secret_key";  // Use a more secure secret in production

        // Generate token for user
        public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            return createToken(claims, userDetails.getUsername());
        }

        // Create the token
        private String createToken(Map<String, Object> claims, String subject) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours validity
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        }

        // Validate token
        public Boolean validateToken(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }

        // Extract username from token
        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        // Extract expiration date from token
        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        // Extract a specific claim from token
        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }

        // Check if the token has expired
        private Boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        // Extract all claims from token
        private Claims extractAllClaims(String token) {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }
    }
    ```

- **JwtRequestFilter.java**:
  - Intercepts incoming requests to validate the JWT token.
  - Example:
    ```java
    @Component
    public class JwtRequestFilter extends OncePerRequestFilter {
        @Autowired
        private CustomUserDetailsService userDetailsService;
        @Autowired
        private JwtUtil jwtUtil;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            String authorizationHeader = request.getHeader("Authorization");
            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            chain.doFilter(request, response);
        }
    }
    ```

- **SecurityConfig.java**:
  - Configures Spring Security to use JWT for authentication and Redis for session management.
  - Example:
    ```java
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private CustomUserDetailsService userDetailsService;
        @Autowired
        private JwtRequestFilter jwtRequestFilter;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                .authorizeRequests().antMatchers("/api/auth/login").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authentication_manager_bean();
        }
    }
    ```

#### **1.6. Implement Authentication Controller**

The controller handles user login and logout, including JWT token generation and session management.

- **JwtAuthenticationController.java**:
  - Handles login, logout, and JWT token generation.
  - Stores user sessions in Redis.
  - Example:
    ```java
    @RestController
    @RequestMapping("/api/auth")
    public class JwtAuthenticationController {
        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private JwtUtil jwtTokenUtil;
        @Autowired
        private CustomUserDetailsService userDetailsService;
        @Autowired
        private RedisTemplate<String, Object> redisTemplate;

        @PostMapping("/login")
        public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {
            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );
            } catch (BadCredentialsException e) {
                throw new Exception("Incorrect username or password", e);
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String jwt = jwtTokenUtil.generateToken(userDetails);
            redisTemplate.opsForSet().add("loggedInUsers", userDetails.getUsername());

            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(HttpServletRequest request) {
            String username = jwtTokenUtil.extractUsername(request.getHeader("Authorization").substring(7));
            redisTemplate.opsForSet().remove("loggedInUsers", username);
            request.getSession().invalidate();
            return ResponseEntity.ok().build();
        }
    }
    ```

#### **1.7. Configure WebSocket with Kafka**

WebSocket allows real-time communication between the client and server, while Kafka is used as the message broker.

- **WebSocketConfig.java**:
  - Configures WebSocket and uses Kafka as the message broker.
  - Example:
    ```java
    @Configuration
    @EnableWebSocketMessageBroker
    @EnableKafka
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableStompBrokerRelay("/topic

")
                  .setRelayHost("kafka")
                  .setRelayPort(9092);
            config.setApplicationDestinationPrefixes("/app");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
        }
    }
    ```

- **NotificationController.java**:
  - Handles the broadcasting of notifications via WebSocket.
  - Example:
    ```java
    @Controller
    public class NotificationController {
        @MessageMapping("/send")
        @SendTo("/topic/notifications")
        public String sendNotification(String message) {
            return message;
        }
    }
    ```

#### **1.8. Implement Kafka Consumer**

The Kafka consumer listens to Kafka topics for changes in the database and sends notifications to logged-in users via WebSocket.

- **NotificationConsumer.java**:
  - Listens for changes in the database and sends notifications to logged-in users via WebSocket.
  - Example:
    ```java
    @Service
    public class NotificationConsumer {
        @Autowired
        private RedisTemplate<String, Object> redisTemplate;
        @Autowired
        private NotificationRepository notificationRepository;
        @Autowired
        private SimpMessagingTemplate messagingTemplate;

        @KafkaListener(topics = "db-notifications", groupId = "notification-group")
        public void consume(String message) {
            Notification notification = // Parse the message into a Notification object
            notificationRepository.save(notification);

            Set<Object> loggedInUsers = redisTemplate.opsForSet().members("loggedInUsers");
            for (Object username : loggedInUsers) {
                messagingTemplate.convertAndSend("/topic/notifications/" + username, notification.getMessage());
            }
        }
    }
    ```

#### **1.9. Set Up Kafka Connect**

Kafka Connect is a framework for connecting Kafka with external systems like databases. We'll configure Kafka Connect to pull data from PostgreSQL and push it to a Kafka topic.

**Custom Kafka Connect Configuration**:

Here's how you might configure Kafka Connect to pull data from PostgreSQL:

```json
{
    "name": "jdbc-source-connector",
    "config": {
        "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
        "tasks.max": "1",
        "connection.url": "jdbc:postgresql://postgres:5432/notificationdb",
        "connection.user": "postgres",
        "connection.password": "postgres",
        "table.whitelist": "notifications",
        "mode": "timestamp",
        "timestamp.column.name": "updated_at",
        "topic.prefix": "db-",
        "poll.interval.ms": "1000"
    }
}
```

This configuration specifies a JDBC source connector that listens to the `notifications` table in the `notificationdb` database and writes changes to a Kafka topic with the prefix `db-`.

To run Kafka Connect:

1. **Start Kafka Connect**:
   - Ensure Kafka and PostgreSQL are running.
   - Start Kafka Connect using Docker Compose (covered below).

2. **Deploy the Connector**:
   - POST the above JSON configuration to Kafka Connect’s REST API.
   - Example:
     ```bash
     curl -X POST -H "Content-Type: application/json" --data @config.json http://localhost:8083/connectors
     ```

3. **Monitor the Topic**:
   - Use a Kafka consumer or UI tool to monitor messages in the `db-notifications` topic.

#### **1.10. Dockerize the Backend**

Dockerizing the application ensures that it can be run consistently across different environments.

- **Dockerfile**:
  - Defines the Docker image for the Spring Boot application.
  - Example:
    ```dockerfile
    FROM openjdk:11-jre-slim
    VOLUME /tmp
    COPY target/notification-system-0.0.1-SNAPSHOT.jar app.jar
    ENTRYPOINT ["java","-jar","/app.jar"]
    ```

- **docker-compose.yml**:
  - Orchestrates the application stack, including Kafka, Redis, PostgreSQL, Spring Boot, and Kafka Connect.
  - Example:
    ```yaml
    version: '3.8'

    services:
      zookeeper:
        image: wurstmeister/zookeeper:3.4.6
        ports:
          - "2181:2181"

      kafka:
        image: wurstmeister/kafka:2.12-2.3.0
        ports:
          - "9092:9092"
        environment:
          KAFKA_ADVERTISED_LISTENERS: INSIDE://kafka:9092,OUTSIDE://localhost:9092
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT
          KAFKA_INTER_BROKER_LISTENER_NAME: INSIDE
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

      connect:
        image: confluentinc/cp-kafka-connect:5.5.1
        environment:
          CONNECT_BOOTSTRAP_SERVERS: 'kafka:9092'
          CONNECT_REST_ADVERTISED_HOST_NAME: 'connect'
          CONNECT_GROUP_ID: 'compose-connect-group'
          CONNECT_CONFIG_STORAGE_TOPIC: 'docker-connect-configs'
          CONNECT_OFFSET_STORAGE_TOPIC: 'docker-connect-offsets'
          CONNECT_STATUS_STORAGE_TOPIC: 'docker-connect-status'
          CONNECT_KEY_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_VALUE_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_INTERNAL_KEY_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_INTERNAL_VALUE_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_REST_PORT: 8083
          CONNECT_PLUGIN_PATH: '/usr/share/java,/etc/kafka-connect/jars'
        volumes:
          - ./plugins:/etc/kafka-connect/jars
        ports:
          - 8083:8083

      postgres:
        image: postgres:12
        environment:
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: notificationdb
        ports:
          - "5432:5432"

      redis:
        image: redis:6.2
        ports:
          - "6379:6379"

      app:
        build: .
        ports:
          - "8080:8080"
        environment:
          SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/notificationdb
          SPRING_DATASOURCE_USERNAME: postgres
          SPRING_DATASOURCE_PASSWORD: postgres
          SPRING_REDIS_HOST: redis
    ```

### **2. Angular Frontend Implementation**

#### **2.1. Create the Angular Project**

- **Setup**:
  - Use Angular CLI to create a new project:
    ```bash
    ng new notification-system-frontend
    cd notification-system-frontend
    npm install @auth0/angular-jwt
    ```
  - **Structure**: Follow the folder structure mentioned earlier.

#### **2.2. Implement Services**

- **AuthService**:
  - Manages user authentication and JWT tokens.
  - Example:
    ```typescript
    import { Injectable } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { JwtHelperService } from '@auth0/angular-jwt';
    import { BehaviorSubject } from 'rxjs';

    @Injectable({
      providedIn: 'root'
    })
    export class AuthService {
      private jwtHelper = new JwtHelperService();
      private authUrl = 'http://localhost:8080/api/auth/login';
      public currentUserSubject: BehaviorSubject<any>;

      constructor(private http: HttpClient) {
        this.currentUserSubject = new BehaviorSubject<any>(localStorage.getItem('currentUser'));
      }

      login(username: string, password: string) {
        return this.http.post<any>(this.authUrl, { username, password }).subscribe(response => {
          const token = response.jwt;
          localStorage.setItem('currentUser', JSON.stringify({ username, token }));
          this.currentUserSubject.next({ username, token });
        });
      }

      logout() {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
        this.http.post('http://localhost:8080/api/auth/logout', {}).subscribe();
      }

      isAuthenticated(): boolean {
        const token = JSON.parse(localStorage.getItem('currentUser'))?.token;
        return token != null && !this.jwtHelper.isTokenExpired(token);
      }
    }
    ```

- **WebSocketService**:
  - Manages WebSocket connections for real-time notifications.
  - Example:
    ```typescript
    import { Injectable } from '@angular/core';
    import { Observable } from 'rxjs';
    import { webSocket } from 'rxjs/webSocket';

    @Injectable({
      providedIn: 'root'
    })
    export class WebSocketService {
      private socket$ = webSocket('ws://localhost:8080/ws');

      connect(): Observable<any> {
        return this.socket$;
      }

      sendMessage(message: any) {
        this.socket$.next(message);
      }

      close() {
        this.socket$.complete();
      }
    }
    ```

#### **2.3. Implement Components**

- **LoginComponent**:
  - Handles user login.
  - Example:
    ```typescript
    import { Component } from '@angular/core';
    import { AuthService } from '../../services/auth.service';
    import { Router } from '@angular/router';

    @Component({
      selector: 'app-login',
      templateUrl: './login.component.html',
      styleUrls: ['./login.component.css']
    })
    export class LoginComponent {
      username: string;
      password: string;

      constructor(private authService: Auth

Service, private router: Router) { }

      login() {
        this.authService.login(this.username, this.password);
        this.router.navigate(['/']);
      }
    }
    ```

  - **HTML Template**:
    ```html
    <div class="login-container">
      <h2>Login</h2>
      <form (submit)="login()">
        <div>
          <label>Username</label>
          <input type="text" [(ngModel)]="username" name="username">
        </div>
        <div>
          <label>Password</label>
          <input type="password" [(ngModel)]="password" name="password">
        </div>
        <button type="submit">Login</button>
      </form>
    </div>
    ```

- **NotificationsComponent**:
  - Displays notifications received via WebSocket.
  - Example:
    ```typescript
    import { Component, OnInit } from '@angular/core';
    import { WebSocketService } from '../../services/websocket.service';

    @Component({
      selector: 'app-notifications',
      templateUrl: './notifications.component.html',
      styleUrls: ['./notifications.component.css']
    })
    export class NotificationsComponent implements OnInit {
      notifications: string[] = [];

      constructor(private webSocketService: WebSocketService) { }

      ngOnInit() {
        this.webSocketService.connect().subscribe(message => {
          this.notifications.push(message);
        });
      }
    }
    ```

  - **HTML Template**:
    ```html
    <div class="notifications-container">
      <h2>Notifications</h2>
      <ul>
        <li *ngFor="let notification of notifications">
          {{ notification }}
        </li>
      </ul>
    </div>
    ```

#### **2.4. Configure Routing**

- **app-routing.module.ts**:
  - Configures the routes for the Angular application.
  - Example:
    ```typescript
    import { NgModule } from '@angular/core';
    import { RouterModule, Routes } from '@angular/router';
    import { LoginComponent } from './components/login/login.component';
    import { NotificationsComponent } from './components/notifications/notifications.component';
    import { AuthGuard } from './guards/auth.guard';

    const routes: Routes = [
      { path: 'login', component: LoginComponent },
      { path: 'notifications', component: NotificationsComponent, canActivate: [AuthGuard] },
      { path: '', redirectTo: '/login', pathMatch: 'full' }
    ];

    @NgModule({
      imports: [RouterModule.forRoot(routes)],
      exports: [RouterModule]
    })
    export class AppRoutingModule { }
    ```

#### **2.5. Set Up JWT Interceptor**

- **JwtInterceptor.ts**:
  - Attaches the JWT token to outgoing HTTP requests.
  - Example:
    ```typescript
    import { Injectable } from '@angular/core';
    import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
    import { Observable } from 'rxjs';

    @Injectable()
    export class JwtInterceptor implements HttpInterceptor {
      intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = JSON.parse(localStorage.getItem('currentUser'))?.token;
        if (token) {
          request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          });
        }
        return next.handle(request);
      }
    }
    ```

### **3. Docker and DevOps**

#### **3.1. Dockerize the Application**

- **Dockerfile** for Spring Boot:
  - Defines the Docker image for the Spring Boot application.
  - Example:
    ```dockerfile
    FROM openjdk:11-jre-slim
    VOLUME /tmp
    COPY target/notification-system-0.0.1-SNAPSHOT.jar app.jar
    ENTRYPOINT ["java","-jar","/app.jar"]
    ```

- **docker-compose.yml**:
  - Orchestrates the entire application stack, including Spring Boot, Angular, Kafka, Redis, and PostgreSQL.
  - Example:
    ```yaml
    version: '3.8'

    services:
      zookeeper:
        image: wurstmeister/zookeeper:3.4.6
        ports:
          - "2181:2181"

      kafka:
        image: wurstmeister/kafka:2.12-2.3.0
        ports:
          - "9092:9092"
        environment:
          KAFKA_ADVERTISED_LISTENERS: INSIDE://kafka:9092,OUTSIDE://localhost:9092
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT
          KAFKA_INTER_BROKER_LISTENER_NAME: INSIDE
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

      connect:
        image: confluentinc/cp-kafka-connect:5.5.1
        environment:
          CONNECT_BOOTSTRAP_SERVERS: 'kafka:9092'
          CONNECT_REST_ADVERTISED_HOST_NAME: 'connect'
          CONNECT_GROUP_ID: 'compose-connect-group'
          CONNECT_CONFIG_STORAGE_TOPIC: 'docker-connect-configs'
          CONNECT_OFFSET_STORAGE_TOPIC: 'docker-connect-offsets'
          CONNECT_STATUS_STORAGE_TOPIC: 'docker-connect-status'
          CONNECT_KEY_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_VALUE_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_INTERNAL_KEY_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_INTERNAL_VALUE_CONVERTER: 'org.apache.kafka.connect.json.JsonConverter'
          CONNECT_REST_PORT: 8083
          CONNECT_PLUGIN_PATH: '/usr/share/java,/etc/kafka-connect/jars'
        volumes:
          - ./plugins:/etc/kafka-connect/jars
        ports:
          - 8083:8083

      postgres:
        image: postgres:12
        environment:
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: notificationdb
        ports:
          - "5432:5432"

      redis:
        image: redis:6.2
        ports:
          - "6379:6379"

      app:
        build: .
        ports:
          - "8080:8080"
        environment:
          SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/notificationdb
          SPRING_DATASOURCE_USERNAME: postgres
          SPRING_DATASOURCE_PASSWORD: postgres
          SPRING_REDIS_HOST: redis
    ```

### **3.2. Running the Application**

1. **Build the Angular Application**:
   ```bash
   ng build --prod
   ```

2. **Run Docker Compose**:
   ```bash
   docker-compose up --build
   ```

3. **Access the Application**:
   - Access the Angular frontend at `http://localhost:4200`.
   - Test the login functionality and observe notifications being received in real-time.

---

## **Conclusion**

This detailed guide covered the complete setup for building a real-time notification system using Spring Boot and Angular, integrated with Kafka, Redis, and Docker. This architecture is suitable for scalable and robust applications requiring real-time features and secure user management.
