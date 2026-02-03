package com.tictactoe.engine;

import com.tictactoe.contracts.GameUpdatedEvent;
import com.tictactoe.contracts.MoveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class GameEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(GameEventPublisher.class);

    private final KafkaTemplate<String, GameUpdatedEvent> kafkaTemplate;
    private final String topic;

    public GameEventPublisher(KafkaTemplate<String, GameUpdatedEvent> kafkaTemplate,
                              @Value("${tictactoe.kafka.topic:tictactoe.game.events.v1}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(GameState state, MoveDto lastMove, UUID correlationId) {
        GameUpdatedEvent event = new GameUpdatedEvent();
        event.setEventId(UUID.randomUUID());
        event.setCorrelationId(correlationId);
        event.setOccurredAt(Instant.now());
        event.setGameId(state.getGameId());
        event.setMoveNumber(state.getMoveNumber());
        event.setLastMove(lastMove);
        event.setStatus(state.getStatus());
        event.setWinner(state.getWinner());
        List<List<String>> board = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            List<String> row = new ArrayList<>();
            for (int c = 0; c < 3; c++) row.add(state.getCell(r, c));
            board.add(row);
        }
        event.setBoard(board);
        // Fire-and-forget in background so Kafka unavailability does not block REST response
        new Thread(() -> {
            try {
                kafkaTemplate.send(topic, state.getGameId().toString(), event);
            } catch (Exception e) {
                log.error("Failed to publish GameUpdatedEvent: gameId={}, correlationId={}", state.getGameId(), correlationId, e);
            }
        }, "kafka-publish").start();
    }
}
