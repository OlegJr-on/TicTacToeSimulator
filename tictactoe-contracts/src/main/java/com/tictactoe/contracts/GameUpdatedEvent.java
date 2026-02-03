package com.tictactoe.contracts;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Kafka event: topic tictactoe.game.events.v1
 */
public class GameUpdatedEvent {
    public static final String EVENT_TYPE = "GAME_UPDATED";
    public static final int EVENT_VERSION = 1;

    private String eventType = EVENT_TYPE;
    private int eventVersion = EVENT_VERSION;
    private UUID eventId;
    private UUID correlationId;
    private Instant occurredAt;
    private UUID gameId;
    private int moveNumber;
    private MoveDto lastMove;
    private GameStatus status;
    private PlayerSymbol winner;
    private List<List<String>> board;

    public GameUpdatedEvent() {}

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public int getEventVersion() { return eventVersion; }
    public void setEventVersion(int eventVersion) { this.eventVersion = eventVersion; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public UUID getCorrelationId() { return correlationId; }
    public void setCorrelationId(UUID correlationId) { this.correlationId = correlationId; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public UUID getGameId() { return gameId; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }
    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }
    public MoveDto getLastMove() { return lastMove; }
    public void setLastMove(MoveDto lastMove) { this.lastMove = lastMove; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    public PlayerSymbol getWinner() { return winner; }
    public void setWinner(PlayerSymbol winner) { this.winner = winner; }
    public List<List<String>> getBoard() { return board; }
    public void setBoard(List<List<String>> board) { this.board = board; }
}
