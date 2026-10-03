package com.example.watchtogether.service;

import com.example.watchtogether.model.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(String userId, String username) {
        String roomCode = generateRoomCode();
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setCode(roomCode);
        room.setHostUserId(userId);

        RoomMember member = new RoomMember();
        member.setUserId(userId);
        member.setUsername(username);
        member.setHost(true);
        member.setOnline(true);

        room.getMembers().add(member);
        rooms.put(roomCode, room);
        return room;
    }

    public Room joinRoom(String roomCode, String userId, String username) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        boolean alreadyExists = room.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(userId));

        if (!alreadyExists) {
            RoomMember member = new RoomMember();
            member.setUserId(userId);
            member.setUsername(username);
            member.setHost(false);
            member.setOnline(true);
            room.getMembers().add(member);
        }

        return room;
    }

    public Room addVideo(String roomCode, String userId, String title, String url, String source) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        VideoItem video = new VideoItem();
        video.setId(UUID.randomUUID().toString());
        video.setTitle(title);
        video.setUrl(normalizeVideoUrl(url, source));
        video.setSource(source);

        room.getQueue().add(video);

        if (room.getCurrentVideo() == null) {
            room.setCurrentVideo(video);
        }

        return room;
    }

    public Room addChatMessage(String roomCode, String userId, String username, String message) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setUserId(userId);
        chatMessage.setUsername(username);
        chatMessage.setMessage(message);
        chatMessage.setCreatedAt(System.currentTimeMillis());

        room.getChatMessages().add(chatMessage);
        return room;
    }

    public Room updatePlaybackState(String roomCode, String userId, String status, long currentTime) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }

        PlaybackState playbackState = room.getPlaybackState();
        playbackState.setStatus(status);
        playbackState.setCurrentTime(currentTime);
        playbackState.setUpdatedAt(System.currentTimeMillis());

        return room;
    }

    public Room getRoom(String roomCode) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        return room;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String normalizeVideoUrl(String url, String source) {
        if (source == null || source.isBlank()) {
            return url;
        }

        String trimmed = url.trim();

        if (source.equalsIgnoreCase("youtube")) {
            if (trimmed.contains("youtube.com/watch?v=")) {
                String videoId = trimmed.split("v=")[1].split("&")[0];
                return "https://www.youtube.com/embed/" + videoId;
            }
            if (trimmed.contains("youtu.be/")) {
                String videoId = trimmed.split("youtu.be/")[1].split("[
?&]")[0];
                return "https://www.youtube.com/embed/" + videoId;
            }
            return trimmed;
        }

        if (source.equalsIgnoreCase("vimeo")) {
            if (trimmed.contains("vimeo.com/")) {
                String vimeoId = trimmed.split("vimeo.com/")[1].split("[
?&]")[0];
                return "https://player.vimeo.com/video/" + vimeoId;
            }
        }

        return trimmed;
    }
}
