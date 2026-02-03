package com.tictactoe.session;

import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.MoveDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.UUID;

@Component
public class EngineClient {

    private static final Logger log = LoggerFactory.getLogger(EngineClient.class);

    private final WebClient webClient;
    private final String engineBaseUrl;

    public EngineClient(WebClient.Builder builder,
                        @Value("${engine.base-url:http://localhost:8081}") String engineBaseUrl) {
        this.engineBaseUrl = engineBaseUrl.replaceAll("/$", "");
        this.webClient = builder
            .baseUrl(this.engineBaseUrl)
            .build();
    }

    public GameDto createGame(UUID correlationId) {
        return webClient.post()
            .uri("/games")
            .header("X-Correlation-Id", correlationId != null ? correlationId.toString() : UUID.randomUUID().toString())
            .retrieve()
            .bodyToMono(GameDto.class)
            .block(Duration.ofSeconds(10));
    }

    public GameDto getGame(UUID gameId, UUID correlationId) {
        return webClient.get()
            .uri("/games/{gameId}", gameId)
            .header("X-Correlation-Id", correlationId != null ? correlationId.toString() : UUID.randomUUID().toString())
            .retrieve()
            .bodyToMono(GameDto.class)
            .block(Duration.ofSeconds(5));
    }

    /**
     * Apply move with retries on 5xx/timeouts. Returns null on non-retryable (4xx) or after giving up.
     */
    public GameDto applyMove(UUID gameId, MoveDto move, UUID correlationId) {
        String corr = correlationId != null ? correlationId.toString() : UUID.randomUUID().toString();
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                GameDto result = webClient.post()
                    .uri("/games/{gameId}/move", gameId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Correlation-Id", corr)
                    .bodyValue(move)
                    .retrieve()
                    .bodyToMono(GameDto.class)
                    .block(Duration.ofSeconds(5));
                return result;
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().is4xxClientError()) {
                    log.warn("Engine returned 4xx: gameId={}, status={}", gameId, e.getStatusCode());
                    return null;
                }
                if (attempt < maxAttempts) {
                    long backoff = (long) Math.pow(2, attempt) * 200;
                    log.warn("Engine error, retrying in {}ms: gameId={}, attempt={}", backoff, gameId, attempt);
                    try { Thread.sleep(backoff); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return null; }
                } else {
                    log.error("Engine unavailable after {} attempts: gameId={}", maxAttempts, gameId, e);
                    throw e;
                }
            }
        }
        return null;
    }
}
