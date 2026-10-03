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

const normalizeUrl = (value: string) => {
  if (!value) return "";
  const trimmed = value.trim();

  if (trimmed.includes("youtube.com/watch?v=")) {
    const match = trimmed.match(/v=([A-Za-z0-9_-]+)/);
    return match ? `https://www.youtube.com/embed/${match[1]}` : trimmed;
  }

  if (trimmed.includes("youtu.be/")) {
    const match = trimmed.match(/youtu\.be\/([A-Za-z0-9_-]+)/);
    return match ? `https://www.youtube.com/embed/${match[1]}` : trimmed;
  }

  return trimmed;
};

const getErrorMessage = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    return error.response?.data?.message || error.message;
  }
  return "Something went wrong";
};

export default function App() {
  const [roomCode, setRoomCode] = useState("TRIAL1");
  const [userName, setUserName] = useState("User");
  const [videoTitle, setVideoTitle] = useState("Funny Cat Compilation");
  const [videoUrl, setVideoUrl] = useState("https://www.youtube.com/watch?v=7KQwA6n5K8I");
  const [chatText, setChatText] = useState("");
  const [room, setRoom] = useState<RoomData | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const playerUrl = useMemo(() => {
    if (!room?.currentVideo?.url) return "";
    return room.currentVideo.url;
  }, [room]);

  const createRoom = async () => {
    if (!userName.trim()) {
      setError("Username is required");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await axios.post(`${API_BASE}/rooms`, {
        userId: `user-${Date.now()}`,
        username: userName.trim()
      });
      setRoom(response.data);
      setRoomCode(response.data.code);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const joinRoom = async () => {
    if (!roomCode.trim() || !userName.trim()) {
      setError("Room code and username are required");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await axios.post(`${API_BASE}/rooms/join`, {
        roomCode: roomCode.trim().toUpperCase(),
        userId: `user-${Date.now()}`,
        username: userName.trim()
      });
      setRoom(response.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const addVideo = async () => {
    if (!room) {
      setError("Create or join a room first");
      return;
    }
    if (!videoTitle.trim() || !videoUrl.trim()) {
      setError("Title and URL are required");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await axios.post(`${API_BASE}/rooms/${room.code}/video`, {
        userId: `user-${Date.now()}`,
        title: videoTitle.trim(),
        url: normalizeUrl(videoUrl),
        source: "youtube"
      });
      setRoom(response.data);
      setVideoTitle("");
      setVideoUrl("");
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const sendMessage = async () => {
    if (!room || !chatText.trim()) return;

    setLoading(true);
    setError("");

    try {
      const response = await axios.post(`${API_BASE}/rooms/${room.code}/chat`, {
        userId: `user-${Date.now()}`,
        username: userName.trim(),
        message: chatText.trim()
      });
      setRoom(response.data);
      setChatText("");
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const syncPlayback = async (status: string, currentTime: number) => {
    if (!room) return;

    setLoading(true);
    setError("");

    try {
      const response = await axios.post(`${API_BASE}/rooms/${room.code}/playback`, {
        userId: `user-${Date.now()}`,
        status,
        currentTime
      });
      setRoom(response.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
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
            <button onClick={createRoom} disabled={loading}>Create room</button>
            <button onClick={joinRoom} className="secondary" disabled={loading}>Join</button>
          </div>
        </div>

        {error && <div className="error-box">{error}</div>}

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
            <button onClick={addVideo} disabled={loading}>Add video</button>
            <button className="secondary" onClick={() => syncPlayback("PLAYING", 10)} disabled={loading}>
              Play
            </button>
            <button className="secondary" onClick={() => syncPlayback("PAUSED", 10)} disabled={loading}>
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
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                sendMessage();
              }
            }}
          />
          <button onClick={sendMessage} disabled={loading}>Send</button>
        </div>
      </aside>
    </div>
  );
}
