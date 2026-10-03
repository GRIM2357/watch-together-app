package com.example.watchtogether.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaybackStateRequest {
    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Playback status is required")
    private String status;

    private long currentTime;
}
