package com.example.notificationsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {

    @JsonProperty("id")
    private long id;

    @JsonProperty("created_at")
    private long createdAt;

    @JsonProperty("message")
    private String message;

    @JsonProperty("user_id")
    private long user_Id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return user_Id;
    }

    public void setUserId(long user_Id) {
        this.user_Id = user_Id;
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", message='" + message + '\'' +
                ", userId=" + user_Id +
                '}';
    }
}
