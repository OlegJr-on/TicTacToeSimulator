package com.tictactoe.session;

import com.tictactoe.contracts.GameUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GameEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(GameEventConsumer.class);

    @KafkaListener(topics = "${tictactoe.kafka.topic:tictactoe.game.events.v1}", groupId = "${spring.kafka.consumer.group-id:session-service}")
    public void consume(GameUpdatedEvent event) {
        try {
            log.debug("Game event received: gameId={}, correlationId={}, moveNumber={}, status={}",
                event.getGameId(), event.getCorrelationId(), event.getMoveNumber(), event.getStatus());
        } catch (Exception e) {
            log.error("Failed to process game event: gameId={}", event.getGameId(), e);
        }
    }
}
