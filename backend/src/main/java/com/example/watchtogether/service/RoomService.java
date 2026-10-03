package com.example.watchtogether.service;

import com.example.watchtogether.exception.InvalidRequestException;
import com.example.watchtogether.exception.RoomNotFoundException;
import com.example.watchtogether.model.ChatMessage;
import com.example.watchtogether.model.PlaybackState;
import com.example.watchtogether.model.Room;
import com.example.watchtogether.model.RoomMember;
import com.example.watchtogether.model.VideoItem;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RoomService {
    private static final Pattern YOUTUBE_WATCH_PATTERN =
            Pattern.compile("^(?:https?://)?(?:www\\.)?youtube\\.com/watch\\?v=([\\w-]+)(?:[&?].*)?$");
    private static final Pattern YOUTUBE_SHORT_PATTERN =
            Pattern.compile("^(?:https?://)?(?:www\\.)?youtu\\.be/([\\w-]+)(?:[&?].*)?$");
    private static final Pattern VIMEO_PATTERN =
            Pattern.compile("^(?:https?://)?(?:www\\.)?vimeo\\.com/(\\d+)(?:[/?#].*)?$");

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(String userId, String username) {
        validateUser(userId, username);

        String roomCode = generateRoomCode();
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setCode(roomCode);
        room.setHostUserId(userId);

        RoomMember member = buildMember(userId, username, true);
        room.getMembers().add(member);
        rooms.put(roomCode, room);

        return room;
    }

    public Room joinRoom(String roomCode, String userId, String username) {
        validateUser(userId, username);
        Room room = getRoomByCode(roomCode);

        boolean alreadyExists = room.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(userId));

        if (!alreadyExists) {
            room.getMembers().add(buildMember(userId, username, false));
        }

        return room;
    }

    public Room addVideo(String roomCode, String userId, String title, String url, String source) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new InvalidRequestException("Room code is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("User id is required");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidRequestException("Video title is required");
        }
        if (url == null || url.isBlank()) {
            throw new InvalidRequestException("Video URL is required");
        }

        Room room = getRoomByCode(roomCode);

        VideoItem video = new VideoItem();
        video.setId(UUID.randomUUID().toString());
        video.setTitle(title.trim());
        video.setUrl(normalizeVideoUrl(url.trim(), source));
        video.setSource(source == null ? "unknown" : source.trim().toLowerCase());

        room.getQueue().add(video);

        if (room.getCurrentVideo() == null) {
            room.setCurrentVideo(video);
        }

        return room;
    }

    public Room addChatMessage(String roomCode, String userId, String username, String message) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new InvalidRequestException("Room code is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("User id is required");
        }
        if (message == null || message.isBlank()) {
            throw new InvalidRequestException("Message cannot be empty");
        }

        Room room = getRoomByCode(roomCode);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setUserId(userId);
        chatMessage.setUsername(username == null ? "Anonymous" : username.trim());
        chatMessage.setMessage(message.trim());
        chatMessage.setCreatedAt(System.currentTimeMillis());

        room.getChatMessages().add(chatMessage);
        return room;
    }

    public Room updatePlaybackState(String roomCode, String userId, String status, long currentTime) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new InvalidRequestException("Room code is required");
        }

        Room room = getRoomByCode(roomCode);

        PlaybackState playbackState = room.getPlaybackState();
        playbackState.setStatus(status == null ? "PAUSED" : status.trim().toUpperCase());
        playbackState.setCurrentTime(Math.max(0, currentTime));
        playbackState.setUpdatedAt(System.currentTimeMillis());

        return room;
    }

    public Room getRoom(String roomCode) {
        return getRoomByCode(roomCode);
    }

    private Room getRoomByCode(String roomCode) {
        Room room = rooms.get(roomCode.trim().toUpperCase());
        if (room == null) {
            throw new RoomNotFoundException(roomCode);
        }
        return room;
    }

    private void validateUser(String userId, String username) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidRequestException("User id is required");
        }
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("Username is required");
        }
    }

    private RoomMember buildMember(String userId, String username, boolean host) {
        RoomMember member = new RoomMember();
        member.setUserId(userId);
        member.setUsername(username.trim());
        member.setHost(host);
        member.setOnline(true);
        return member;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String normalizeVideoUrl(String url, String source) {
        String trimmed = url.trim();

        if (source != null && source.equalsIgnoreCase("youtube")) {
            Matcher watchMatcher = YOUTUBE_WATCH_PATTERN.matcher(trimmed);
            if (watchMatcher.matches()) {
                return "https://www.youtube.com/embed/" + watchMatcher.group(1);
            }

            Matcher shortMatcher = YOUTUBE_SHORT_PATTERN.matcher(trimmed);
            if (shortMatcher.matches()) {
                return "https://www.youtube.com/embed/" + shortMatcher.group(1);
            }
        }

        if (source != null && source.equalsIgnoreCase("vimeo")) {
            Matcher vimeoMatcher = VIMEO_PATTERN.matcher(trimmed);
            if (vimeoMatcher.matches()) {
                return "https://player.vimeo.com/video/" + vimeoMatcher.group(1);
            }
        }

        return trimmed;
    }
}
