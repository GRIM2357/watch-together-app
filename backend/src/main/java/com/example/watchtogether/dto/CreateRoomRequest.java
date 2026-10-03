package com.example.watchtogether.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String userId;
    private String username;
}
