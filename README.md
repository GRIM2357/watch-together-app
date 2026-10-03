# Watch Together App

A web application where people can watch random content together in real-time with synchronized playback and chat.

## Features
- Create and join rooms
- Synchronized video playback across multiple users
- Support for YouTube, Vimeo, Twitch, and other platforms
- Random video selection
- Queue system for upcoming videos
- Real-time chat
- Host controls (pause, play, skip, randomize)
- User presence and online status
- Firebase authentication

## Tech Stack
- **Frontend**: React + TypeScript
- **Backend**: Spring Boot + Java
- **Database**: Firebase Firestore
- **Authentication**: Firebase Auth
- **Real-time Communication**: Firebase Realtime Database / WebSockets

## Project Structure
```
.
├── backend/          # Spring Boot application
├── frontend/         # React application
└── README.md
```

## Getting Started

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm start
```

## Environment Variables
Create `.env` files in both `backend/` and `frontend/` directories with Firebase credentials.
