package com.tictactoe.engine;

import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.GameStatus;
import com.tictactoe.contracts.MoveDto;
import com.tictactoe.contracts.PlayerSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameEventPublisher eventPublisher;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(eventPublisher);
    }

    @Test
    void createGame() {
        GameDto dto = gameService.createGame();
        assertNotNull(dto.getGameId());
        assertEquals(GameStatus.IN_PROGRESS, dto.getStatus());
        assertEquals(0, dto.getMoveNumber());
        assertEquals(PlayerSymbol.X, dto.getNextPlayer());
    }

    @Test
    void applyMoveAndIdempotency() {
        GameDto created = gameService.createGame();
        UUID gameId = created.getGameId();
        MoveDto move = new MoveDto(UUID.randomUUID(), PlayerSymbol.X, 0, 0, Instant.now());

        GameDto after = gameService.applyMove(gameId, move, UUID.randomUUID());
        assertEquals(1, after.getMoveNumber());
        assertEquals(PlayerSymbol.O, after.getNextPlayer());
        assertEquals("X", after.getBoard().getRows().get(0).get(0));

        GameDto idem = gameService.applyMove(gameId, move, UUID.randomUUID());
        assertEquals(1, idem.getMoveNumber());
        assertEquals("X", idem.getBoard().getRows().get(0).get(0));
    }

    @Test
    void invalidMoveOccupied() {
        GameDto created = gameService.createGame();
        UUID gameId = created.getGameId();
        gameService.applyMove(gameId, new MoveDto(UUID.randomUUID(), PlayerSymbol.X, 0, 0, Instant.now()), null);
        assertThrows(com.tictactoe.common.ProblemException.class, () ->
            gameService.applyMove(gameId, new MoveDto(UUID.randomUUID(), PlayerSymbol.O, 0, 0, Instant.now()), null));
    }

    @Test
    void invalidMoveWrongTurn() {
        GameDto created = gameService.createGame();
        UUID gameId = created.getGameId();
        assertThrows(com.tictactoe.common.ProblemException.class, () ->
            gameService.applyMove(gameId, new MoveDto(UUID.randomUUID(), PlayerSymbol.O, 0, 0, Instant.now()), null));
    }
}
