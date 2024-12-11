# Battleship Client-Server Application

The **Battleship Client-Server Application** is a Java-based implementation of the classic Battleship game, designed to enable multiplayer gameplay over a network using a robust client-server architecture. This project showcases a seamless integration of network programming, concurrent processing, and modern software design principles.

## Key Features

- **Client-Server Architecture**: 
  - The server manages game sessions and coordinates interactions between players.
  - The client provides an intuitive interface for users to place ships and make moves during gameplay.

- **Real-Time Communication**: 
  - Utilizes **Java Sockets** to ensure low-latency and reliable communication between clients and the server.

- **MVC Design Pattern**: 
  - Separates game logic, user interface, and control flow, ensuring a clean and maintainable codebase.

- **Build Automation**:
  - Employs **Maven** for dependency management and build automation, streamlining the development process.

## Technologies Used

- **Programming Language**: Java
- **Networking**: Java Sockets
- **Design Principles**: Model-View-Controller (MVC)
- **Build Tools**: Maven

## How It Works

1. **Server Initialization**: 
   - The server is started to listen for incoming client connections and manage game sessions.

2. **Client Interaction**:
   - Players connect to the server through the client interface, place their ships, and take turns making moves.

3. **Real-Time Updates**:
   - The server communicates with clients to update the game state dynamically after every move.
