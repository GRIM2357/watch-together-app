package com.example.watchtogether.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String userId;
    private String username;
    private String message;
}
