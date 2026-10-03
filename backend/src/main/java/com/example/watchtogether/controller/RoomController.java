package com.example.watchtogether.controller;

import com.example.watchtogether.dto.*;
import com.example.watchtogether.model.Room;
import com.example.watchtogether.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public Room createRoom(@RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request.getUserId(), request.getUsername());
    }

    @GetMapping("/{roomCode}")
    public Room getRoom(@PathVariable String roomCode) {
        return roomService.getRoom(roomCode);
    }

    @PostMapping("/join")
    public Room joinRoom(@RequestBody JoinRoomRequest request) {
        return roomService.joinRoom(request.getRoomCode(), request.getUserId(), request.getUsername());
    }

    @PostMapping("/{roomCode}/video")
    public Room addVideo(@PathVariable String roomCode, @RequestBody VideoRequest request) {
        return roomService.addVideo(
                roomCode,
                request.getUserId(),
                request.getTitle(),
                request.getUrl(),
                request.getSource()
        );
    }

    @PostMapping("/{roomCode}/chat")
    public Room addChatMessage(@PathVariable String roomCode, @RequestBody ChatRequest request) {
        return roomService.addChatMessage(
                roomCode,
                request.getUserId(),
                request.getUsername(),
                request.getMessage()
        );
    }

    @PostMapping("/{roomCode}/playback")
    public Room updatePlaybackState(@PathVariable String roomCode, @RequestBody PlaybackStateRequest request) {
        return roomService.updatePlaybackState(
                roomCode,
                request.getUserId(),
                request.getStatus(),
                request.getCurrentTime()
        );
    }
}
