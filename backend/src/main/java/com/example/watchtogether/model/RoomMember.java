package com.example.watchtogether.model;

import lombok.Data;

@Data
public class RoomMember {
    private String userId;
    private String username;
    private boolean isHost;
    private boolean online;
}
