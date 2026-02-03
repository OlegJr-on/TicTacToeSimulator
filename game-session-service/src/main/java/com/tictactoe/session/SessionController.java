package com.tictactoe.session;

import com.tictactoe.contracts.SessionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionDto> createSession(
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId) {
        UUID corr = correlationId != null ? correlationId : UUID.randomUUID();
        SessionDto dto = sessionService.createSession(corr);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{sessionId}/simulate")
    public ResponseEntity<SessionDto> simulate(
            @PathVariable UUID sessionId,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId) {
        UUID corr = correlationId != null ? correlationId : UUID.randomUUID();
        sessionService.startSimulation(sessionId, corr);
        SessionDto dto = sessionService.getSession(sessionId);
        return ResponseEntity.accepted().body(dto);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDto> getSession(@PathVariable UUID sessionId) {
        SessionDto dto = sessionService.getSession(sessionId);
        return ResponseEntity.ok(dto);
    }
}
