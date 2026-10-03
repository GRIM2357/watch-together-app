package com.example.watchtogether.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Message is required")
    private String message;
}
