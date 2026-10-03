package com.example.watchtogether.dto;

import lombok.Data;

@Data
public class PlaybackStateRequest {
    private String userId;
    private String status;
    private long currentTime;
}
