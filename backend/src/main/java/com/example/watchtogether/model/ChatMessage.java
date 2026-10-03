package com.example.watchtogether.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String id;
    private String userId;
    private String username;
    private String message;
    private long createdAt;
}
