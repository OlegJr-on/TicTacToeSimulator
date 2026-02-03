# Tic Tac Toe Simulator

Distributed Tic Tac Toe with three services: **Game Engine**, **Game Session**, **UI**. REST for commands; Kafka for game events.

**Project root folder:** Use the name `TicTacToeSimulator` for the repo root (e.g. rename from any other name: `mv <old-name> TicTacToeSimulator`).

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker and Docker Compose (for Kafka)

## Quick start

1. **Start Kafka** (from repo root):

   ```bash
   docker-compose up -d
   ```

   This starts Redpanda and creates topic `tictactoe.game.events.v1`.

2. **Run services** (each in its own terminal, from repo root):

   ```bash
   ./mvnw spring-boot:run -pl game-engine-service
   ./mvnw spring-boot:run -pl game-session-service
   ./mvnw spring-boot:run -pl ui-service
   ```

   Or build and run JARs:

   ```bash
   ./mvnw package -DskipTests
   java -jar game-engine-service/target/game-engine-service-1.0.0-SNAPSHOT.jar
   java -jar game-session-service/target/game-session-service-1.0.0-SNAPSHOT.jar
   java -jar ui-service/target/ui-service-1.0.0-SNAPSHOT.jar
   ```

3. **Open UI**: http://localhost:8080

   Click "Start Simulation", then watch the session view (board + status + move history). The page polls the Session Service every 500ms.

## Configuration

| Service | Port | Env / property |
|---------|------|-----------------|
| UI | 8080 | `SESSION_BASE_URL` (default http://localhost:8082) |
| Session | 8082 | `ENGINE_BASE_URL` (default http://localhost:8081), `KAFKA_BOOTSTRAP_SERVERS` (default localhost:9092) |
| Engine | 8081 | `KAFKA_BOOTSTRAP_SERVERS` (default localhost:9092) |

## Module layout

- **tictactoe-contracts** – DTOs, enums, `GameUpdatedEvent`, error codes
- **tictactoe-common** – Correlation filter (`X-Correlation-Id`), RFC7807 handler
- **game-engine-service** – Board state, rules, `POST/GET /games`, `POST /games/{id}/move`, Kafka producer
- **game-session-service** – Sessions, `POST /sessions`, `POST /sessions/{id}/simulate` (202), `GET /sessions/{id}`, Engine client, Kafka consumer
- **ui-service** – Thymeleaf UI, polling Session for updates

## API summary

- **Engine**: `POST /games`, `GET /games/{gameId}`, `POST /games/{gameId}/move`
- **Session**: `POST /sessions`, `POST /sessions/{sessionId}/simulate`, `GET /sessions/{sessionId}`
- Errors: `application/problem+json` with `errorCode`, `correlationId`

## Tests

```bash
./mvnw test
```

- **Unit tests**: Engine rules and move generator (GameRulesTest, GameServiceTest, MoveGeneratorTest).
- **Integration test**: Session flow (SessionIntegrationTest) — create session, start simulate, poll until FINISHED with WIN; uses WireMock for Engine.
