import { useMemo, useState } from "react";
import axios from "axios";

interface RoomMember {
  userId: string;
  username: string;
  isHost: boolean;
  online: boolean;
}

interface VideoItem {
  id: string;
  title: string;
  url: string;
  source: string;
}

interface ChatMessage {
  id: string;
  username: string;
  message: string;
}

interface RoomData {
  code: string;
  hostUserId: string;
  members: RoomMember[];
  currentVideo?: VideoItem;
  queue: VideoItem[];
  chatMessages: ChatMessage[];
  playbackState?: {
    status: string;
    currentTime: number;
  };
}

const API_BASE = "http://localhost:8080/api";

export default function App() {
  const [roomCode, setRoomCode] = useState("TRIAL1");
  const [userName, setUserName] = useState("User");
  const [videoTitle, setVideoTitle] = useState("Funny Cat Compilation");
  const [videoUrl, setVideoUrl] = useState("https://www.youtube.com/watch?v=7KQwA6n5K8I");
  const [chatText, setChatText] = useState("");
  const [room, setRoom] = useState<RoomData | null>(null);

  const playerUrl = useMemo(() => {
    if (!room?.currentVideo?.url) return "";
    return room.currentVideo.url;
  }, [room]);

  const createRoom = async () => {
    const response = await axios.post(`${API_BASE}/rooms`, {
      userId: "user-1",
      username: userName
    });
    setRoom(response.data);
    setRoomCode(response.data.code);
  };

  const joinRoom = async () => {
    const response = await axios.post(`${API_BASE}/rooms/join`, {
      roomCode,
      userId: "user-2",
      username: userName
    });
    setRoom(response.data);
  };

  const addVideo = async () => {
    if (!room) return;

    const response = await axios.post(`${API_BASE}/rooms/${room.code}/video`, {
      userId: "user-1",
      title: videoTitle,
      url: videoUrl,
      source: "youtube"
    });

    setRoom(response.data);
  };

  const sendMessage = async () => {
    if (!room || !chatText.trim()) return;

    const response = await axios.post(`${API_BASE}/rooms/${room.code}/chat`, {
      userId: "user-1",
      username: userName,
      message: chatText
    });

    setRoom(response.data);
    setChatText("");
  };

  const syncPlayback = async (status: string, currentTime: number) => {
    if (!room) return;

    const response = await axios.post(`${API_BASE}/rooms/${room.code}/playback`, {
      userId: "user-1",
      status,
      currentTime
    });

    setRoom(response.data);
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">Watch Together</div>

        <div className="stack">
          <label>
            Name
            <input value={userName} onChange={(e) => setUserName(e.target.value)} />
          </label>

          <label>
            Room code
            <input value={roomCode} onChange={(e) => setRoomCode(e.target.value)} />
          </label>

          <div className="row">
            <button onClick={createRoom}>Create room</button>
            <button onClick={joinRoom} className="secondary">Join</button>
          </div>
        </div>

        <div className="panel">
          <h3>Room members</h3>
          {room?.members?.length ? (
            room.members.map((member) => (
              <div key={member.userId} className="member-row">
                <span>{member.username}</span>
                <span className={member.online ? "online" : "offline"}>
                  {member.online ? "online" : "offline"}
                </span>
              </div>
            ))
          ) : (
            <p>No members yet</p>
          )}
        </div>
      </aside>

      <main className="main-panel">
        <div className="video-panel">
          {playerUrl ? (
            <iframe
              src={playerUrl}
              title="Watch Together Player"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              allowFullScreen
            />
          ) : (
            <div className="empty-player">
              <p>No video selected yet.</p>
            </div>
          )}
        </div>

        <div className="controls panel">
          <h3>Queue a video</h3>
          <div className="field-grid">
            <input
              value={videoTitle}
              onChange={(e) => setVideoTitle(e.target.value)}
              placeholder="Video title"
            />
            <input
              value={videoUrl}
              onChange={(e) => setVideoUrl(e.target.value)}
              placeholder="Video URL"
            />
          </div>

          <div className="row">
            <button onClick={addVideo}>Add video</button>
            <button className="secondary" onClick={() => syncPlayback("PLAYING", 10)}>
              Play
            </button>
            <button className="secondary" onClick={() => syncPlayback("PAUSED", 10)}>
              Pause
            </button>
          </div>
        </div>
      </main>

      <aside className="chat-panel">
        <h3>Chat</h3>
        <div className="chat-box">
          {room?.chatMessages?.length ? (
            room.chatMessages.map((message, index) => (
              <div key={`${message.id}-${index}`} className="chat-message">
                <strong>{message.username}</strong>
                <span>{message.message}</span>
              </div>
            ))
          ) : (
            <p>Room chat is empty.</p>
          )}
        </div>

        <div className="chat-input">
          <input
            value={chatText}
            onChange={(e) => setChatText(e.target.value)}
            placeholder="Say something..."
          />
          <button onClick={sendMessage}>Send</button>
        </div>
      </aside>
    </div>
  );
}
