# Watch Together MVP

## Overview
A Watch2Gether-style web app for watching content together in real time.

## Stack
- Frontend: React + Vite + TypeScript
- Backend: Spring Boot + Java
- Auth / realtime sync: Firebase-ready

## Features
- Create and join room
- Video queue
- Host controls
- Chat panel
- Playback state sync
- Randomized room flow

## Run

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Then open http://localhost:3000

## Notes
- Add your Firebase credentials in `backend/src/main/resources/firebase-service-account.json`
- The backend is intentionally implemented as a lightweight MVP in-memory room store for initial use
