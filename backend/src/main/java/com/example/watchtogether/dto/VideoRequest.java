package com.example.watchtogether.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoRequest {
    @NotBlank(message = "User id is required")
    private String userId;

    @NotBlank(message = "Video title is required")
    private String title;

    @NotBlank(message = "Video URL is required")
    private String url;

    @NotBlank(message = "Source is required")
    private String source;
}
