package com.tictactoe.session;

import com.tictactoe.contracts.*;
import com.tictactoe.common.ProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import static com.tictactoe.contracts.ErrorCodes.*;

@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final ConcurrentHashMap<UUID, SessionState> sessions = new ConcurrentHashMap<>();
    private final EngineClient engineClient;
    private final SimulationRunner simulationRunner;
    private final MeterRegistry registry;
    private final AtomicInteger simulationsRunning = new AtomicInteger(0);

    public SessionService(EngineClient engineClient, SimulationRunner simulationRunner, MeterRegistry registry) {
        this.engineClient = engineClient;
        this.simulationRunner = simulationRunner;
        this.registry = registry;
        Gauge.builder("sessions.simulations.running", simulationsRunning, AtomicInteger::get).register(registry);
    }

    @org.springframework.context.event.EventListener
    public void onSimulationFinished(SimulationFinishedEvent event) {
        simulationsRunning.decrementAndGet();
    }

    public SessionState getState(UUID sessionId) {
        return sessions.get(sessionId);
    }

    public GameDto getGameSnapshot(UUID sessionId) {
        SessionState state = sessions.get(sessionId);
        if (state == null) return null;
        try {
            return engineClient.getGame(state.getGameId(), null);
        } catch (Exception e) {
            return state.getLastGameSnapshot();
        }
    }

    public SessionDto createSession(UUID correlationId) {
        UUID sessionId = UUID.randomUUID();
        GameDto initialGame = engineClient.createGame(correlationId);
        UUID gameId = initialGame.getGameId();
        SessionState state = new SessionState(sessionId, gameId, initialGame);
        sessions.put(sessionId, state);
        registry.counter("sessions.created").increment();
        log.info("Session created: correlationId={}, sessionId={}, gameId={}", correlationId, sessionId, gameId);
        return toDto(state);
    }

    public void startSimulation(UUID sessionId, UUID correlationId) {
        SessionState state = sessions.get(sessionId);
        if (state == null) throw new ProblemException(HttpStatus.NOT_FOUND.value(), "Session not found", "Session not found: " + sessionId, SESSION_NOT_FOUND);
        if (!state.getRunningLock().compareAndSet(false, true)) {
            throw new ProblemException(HttpStatus.CONFLICT.value(), "Simulation already running", "Simulation is already running for this session", SIMULATION_ALREADY_RUNNING);
        }
        simulationsRunning.incrementAndGet();
        simulationRunner.runAsync(sessionId, correlationId);
    }

    public SessionDto getSession(UUID sessionId) {
        SessionState state = sessions.get(sessionId);
        if (state == null) throw new ProblemException(HttpStatus.NOT_FOUND.value(), "Session not found", "Session not found: " + sessionId, SESSION_NOT_FOUND);
        GameDto snapshot = state.getLastGameSnapshot();
        if (snapshot == null) snapshot = getGameSnapshot(sessionId);
        if (snapshot != null) state.setLastGameSnapshot(snapshot);
        return toDto(state);
    }

    private SessionDto toDto(SessionState state) {
        SessionDto dto = new SessionDto();
        dto.setSessionId(state.getSessionId());
        dto.setGameId(state.getGameId());
        dto.setSimulationStatus(state.getSimulationStatus());
        dto.setGame(state.getLastGameSnapshot());
        dto.setMoves(new ArrayList<>(state.getMoves()));
        return dto;
    }
}
