package com.tictactoe.engine;

import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.MoveDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId) {
        GameDto dto = gameService.createGame();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDto> getGame(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameDto> applyMove(
            @PathVariable UUID gameId,
            @RequestBody MoveDto move,
            @RequestHeader(value = "X-Correlation-Id", required = false) UUID correlationId) {
        GameDto dto = gameService.applyMove(gameId, move, correlationId);
        return ResponseEntity.ok(dto);
    }
}
