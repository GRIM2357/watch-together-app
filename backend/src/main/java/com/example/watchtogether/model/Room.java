package com.example.watchtogether.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Room {
    private String id;
    private String code;
    private String hostUserId;
    private List<RoomMember> members = new ArrayList<>();
    private VideoItem currentVideo;
    private List<VideoItem> queue = new ArrayList<>();
    private PlaybackState playbackState = new PlaybackState();
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
