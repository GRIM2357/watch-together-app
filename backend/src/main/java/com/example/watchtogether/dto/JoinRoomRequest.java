package com.example.watchtogether.dto;

import lombok.Data;

@Data
public class JoinRoomRequest {
    private String roomCode;
    private String userId;
    private String username;
}
