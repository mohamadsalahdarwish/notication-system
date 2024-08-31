package com.example.notificationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NotificationsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsystemApplication.class, args);
	}

}
