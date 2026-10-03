package com.example.watchtogether.model;

import lombok.Data;

@Data
public class PlaybackState {
    private String status = "PAUSED";
    private long currentTime;
    private long updatedAt;
}
