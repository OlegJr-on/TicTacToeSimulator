package com.tictactoe.session;

import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.GameStatus;
import com.tictactoe.contracts.MoveDto;
import com.tictactoe.contracts.PlayerSymbol;
import com.tictactoe.contracts.SimulationStatus;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.UUID;

@Component
public class SimulationRunner {

    private static final Logger log = LoggerFactory.getLogger(SimulationRunner.class);
    private static final int DELAY_MS = 300;

    private final EngineClient engineClient;
    private final SessionService sessionService;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRegistry registry;

    public SimulationRunner(EngineClient engineClient, @Lazy SessionService sessionService,
                           ApplicationEventPublisher eventPublisher, MeterRegistry registry) {
        this.engineClient = engineClient;
        this.sessionService = sessionService;
        this.eventPublisher = eventPublisher;
        this.registry = registry;
    }

    public void runAsync(UUID sessionId, UUID correlationId) {
        Thread t = new Thread(() -> run(sessionId, correlationId), "simulation-" + sessionId);
        t.setDaemon(false);
        t.start();
    }

    void run(UUID sessionId, UUID correlationId) {
        SessionState state = sessionService.getState(sessionId);
        if (state == null) return;
        // Lock already acquired by SessionService.startSimulation; we just run and release in finally

        state.setSimulationStatus(SimulationStatus.RUNNING);
        state.setStartedAt(Instant.now());
        UUID gameId = state.getGameId();
        GameDto game = state.getLastGameSnapshot();

        try {
            while (game != null && game.getStatus() == GameStatus.IN_PROGRESS) {
                PlayerSymbol next = game.getNextPlayer();
                if (next == null) break;
                int[] cell = MoveGenerator.pickMove(game, next);
                if (cell == null) break;

                MoveDto move = new MoveDto(UUID.randomUUID(), next, cell[0], cell[1], Instant.now());
                GameDto result;
                try {
                    result = engineClient.applyMove(gameId, move, correlationId);
                } catch (WebClientResponseException e) {
                    if (e.getStatusCode().value() == 503 || e.getStatusCode().is5xxServerError()) {
                        log.error("Engine unavailable: sessionId={}, marking FAILED", sessionId);
                        registry.counter("moves.failed").increment();
                        state.setSimulationStatus(SimulationStatus.FAILED);
                        state.setFinishedAt(Instant.now());
                        state.getRunningLock().set(false);
                        eventPublisher.publishEvent(new SimulationFinishedEvent(sessionId));
                        return;
                    }
                    result = null;
                }
                if (result == null) {
                    registry.counter("moves.failed").increment();
                    continue;
                }
                registry.counter("moves.sent").increment();
                state.getMoves().add(move);
                state.setLastGameSnapshot(result);
                game = result;
                sleep(DELAY_MS);
            }
            state.setSimulationStatus(SimulationStatus.FINISHED);
            state.setLastGameSnapshot(sessionService.getGameSnapshot(sessionId));
        } catch (Exception e) {
            log.error("Simulation error: sessionId={}", sessionId, e);
            state.setSimulationStatus(SimulationStatus.FAILED);
        } finally {
            state.setFinishedAt(Instant.now());
            state.getRunningLock().set(false);
            eventPublisher.publishEvent(new SimulationFinishedEvent(sessionId));
        }
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
