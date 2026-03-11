# Tetrecs – Grid-Based Puzzle Game

A fast-paced grid-based puzzle game built in Java with JavaFX, featuring single-player and multiplayer modes, animated piece placement, score tracking, and an online leaderboard. Developed as coursework at the University of Southampton.

## Features

- **Single Player Mode** — Place randomised pieces onto a 5×5 grid, clear full rows and columns to score points, with increasing speed as you level up
- **Multiplayer Mode** — Compete against other players in real-time via WebSocket, with a shared leaderboard and in-game chat
- **15 Unique Game Pieces** — Each with rotation support and colour-coded rendering
- **Keyboard & Mouse Controls** — Full WASD/arrow key navigation alongside mouse interaction
- **Piece Preview** — See both the current and upcoming piece, with click-to-rotate and click-to-swap
- **Animated Effects** — Line clear fade-out animations, block hover effects, and a countdown timer bar that changes colour
- **Scoring System** — Multiplier-based scoring with level progression and local/online high score persistence
- **Sound & Music** — Background music and sound effects for placement, rotation, line clears, and level-ups

## Architecture
```
uk.ac.soton.comp1206/
├── App.java / Launcher.java       # Application entry point
├── component/                      # UI components
│   ├── GameBlock.java              # Individual grid block (Canvas)
│   ├── GameBoard.java              # Visual grid (GridPane of GameBlocks)
│   ├── PieceBoard.java             # Piece preview display
│   ├── ScoresList.java             # Animated score list component
│   └── Leaderboard.java            # Multiplayer leaderboard
├── event/                          # Listener interfaces
│   ├── BlockClickedListener.java
│   ├── NextPieceListener.java
│   ├── LineClearedListener.java
│   ├── GameLoopListener.java
│   └── GameEndListener.java
├── game/                           # Game logic
│   ├── Grid.java                   # Grid data model
│   ├── GamePiece.java              # Piece definitions and rotation
│   ├── Game.java                   # Single player game logic
│   └── MultiplayerGame.java        # Multiplayer game logic
├── network/
│   └── Communicator.java           # WebSocket client
├── scene/                          # Game screens
│   ├── MenuScene.java              # Main menu with animated title
│   ├── ChallengeScene.java         # Single player game screen
│   ├── MultiplayerScene.java       # Multiplayer game screen
│   ├── LobbyScene.java             # Multiplayer lobby and chat
│   ├── ScoresScene.java            # End-game scores and leaderboard
│   ├── InstructionsScene.java      # How-to-play with piece gallery
│   └── Multimedia.java             # Audio/music player
└── ui/
    ├── GamePane.java               # Auto-scaling display pane
    └── GameWindow.java             # Main window and scene manager
```

## Tech Stack

- **Language:** Java
- **UI Framework:** JavaFX
- **Networking:** WebSockets (neovisionaries ws-client)
- **Build:** Maven
- **Logging:** Log4j2

## Controls

| Key | Action |
|---|---|
| WASD / Arrow Keys | Navigate grid |
| Enter / X | Place piece |
| Space / R | Swap current and next piece |
| Q / Z / [ | Rotate left |
| E / C / ] | Rotate right |
| Escape | Return to menu |
| Left click (piece preview) | Rotate piece |
| Right click (game board) | Rotate piece |

## Coursework Context

Built for **COMP1206 – Programming II** at the University of Southampton. The project demonstrates object-oriented design with JavaFX, event-driven architecture, real-time multiplayer networking, and custom UI component development.
