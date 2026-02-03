package com.tictactoe.session;

import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.MoveDto;
import com.tictactoe.contracts.SimulationStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionState {
    private final UUID sessionId;
    private final UUID gameId;
    private volatile SimulationStatus simulationStatus = SimulationStatus.CREATED;
    private volatile GameDto lastGameSnapshot;
    private final List<MoveDto> moves = new ArrayList<>();
    private volatile Instant startedAt;
    private volatile Instant finishedAt;
    private final AtomicBoolean runningLock = new AtomicBoolean(false);

    public SessionState(UUID sessionId, UUID gameId, GameDto initialGame) {
        this.sessionId = sessionId;
        this.gameId = gameId;
        this.lastGameSnapshot = initialGame;
    }

    public UUID getSessionId() { return sessionId; }
    public UUID getGameId() { return gameId; }
    public SimulationStatus getSimulationStatus() { return simulationStatus; }
    public void setSimulationStatus(SimulationStatus simulationStatus) { this.simulationStatus = simulationStatus; }
    public GameDto getLastGameSnapshot() { return lastGameSnapshot; }
    public void setLastGameSnapshot(GameDto lastGameSnapshot) { this.lastGameSnapshot = lastGameSnapshot; }
    public List<MoveDto> getMoves() { return moves; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }
    public AtomicBoolean getRunningLock() { return runningLock; }
}
