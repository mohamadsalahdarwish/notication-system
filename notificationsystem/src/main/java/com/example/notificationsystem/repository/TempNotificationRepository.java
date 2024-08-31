package com.example.notificationsystem.repository;

import com.example.notificationsystem.entity.TempNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempNotificationRepository extends JpaRepository<TempNotification, Long> {
    List<TempNotification> findByUsername(String username);
}