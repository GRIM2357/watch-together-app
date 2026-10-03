package com.example.watchtogether.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinRoomRequest {
    @NotBlank(message = "Room code is required")
    private String roomCode;

    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Username is required")
    private String username;
}
