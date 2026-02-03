package com.tictactoe.contracts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionDto {
    private UUID sessionId;
    private UUID gameId;
    private SimulationStatus simulationStatus;
    private GameDto game;
    private List<MoveDto> moves = new ArrayList<>();

    public SessionDto() {}

    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public UUID getGameId() { return gameId; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }
    public SimulationStatus getSimulationStatus() { return simulationStatus; }
    public void setSimulationStatus(SimulationStatus simulationStatus) { this.simulationStatus = simulationStatus; }
    public GameDto getGame() { return game; }
    public void setGame(GameDto game) { this.game = game; }
    public List<MoveDto> getMoves() { return moves; }
    public void setMoves(List<MoveDto> moves) { this.moves = moves != null ? moves : new ArrayList<>(); }
}
