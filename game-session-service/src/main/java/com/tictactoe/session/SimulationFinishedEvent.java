package com.tictactoe.session;

import java.util.UUID;

public class SimulationFinishedEvent {
    private final UUID sessionId;

    public SimulationFinishedEvent(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return sessionId;
    }
}
