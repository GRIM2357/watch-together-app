package com.example.watchtogether.dto;

import lombok.Data;

@Data
public class VideoRequest {
    private String userId;
    private String title;
    private String url;
    private String source;
}
