package com.example.watchtogether.model;

import lombok.Data;

@Data
public class VideoItem {
    private String id;
    private String title;
    private String url;
    private String source;
    private String thumbnailUrl;
}
