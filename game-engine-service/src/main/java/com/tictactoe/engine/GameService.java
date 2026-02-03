package com.tictactoe.engine;

import com.tictactoe.contracts.*;
import com.tictactoe.common.ProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.tictactoe.contracts.ErrorCodes.*;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final ConcurrentHashMap<UUID, GameState> games = new ConcurrentHashMap<>();
    private final GameEventPublisher eventPublisher;

    public GameService(GameEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public GameDto createGame() {
        UUID gameId = UUID.randomUUID();
        GameState state = new GameState(gameId);
        games.put(gameId, state);
        return toDto(state);
    }

    public GameDto getGame(UUID gameId) {
        GameState state = games.get(gameId);
        if (state == null) throw new ProblemException(HttpStatus.NOT_FOUND.value(), "Game not found", "Game not found: " + gameId, GAME_NOT_FOUND);
        return toDto(state);
    }

    public GameDto applyMove(UUID gameId, MoveDto move, UUID correlationId) {
        GameState state = games.get(gameId);
        if (state == null) throw new ProblemException(HttpStatus.NOT_FOUND.value(), "Game not found", "Game not found: " + gameId, GAME_NOT_FOUND);

        if (state.getStatus() != GameStatus.IN_PROGRESS)
            throw new ProblemException(HttpStatus.CONFLICT.value(), "Game finished", "Cannot move after game ended", GAME_FINISHED);

        if (state.getAppliedMoveIds().contains(move.getMoveId())) {
            log.debug("Idempotent move accepted: gameId={}, moveId={}", gameId, move.getMoveId());
            return toDto(state);
        }

        validateMove(state, move);

        state.setCell(move.getRow(), move.getCol(), move.getPlayer().name());
        state.setMoveNumber(state.getMoveNumber() + 1);
        state.getAppliedMoveIds().add(move.getMoveId());

        GameStatus status = GameRules.checkWin(state.getBoard());
        if (status == GameStatus.WIN) {
            state.setStatus(GameStatus.WIN);
            state.setWinner(GameRules.getWinner(state.getBoard()));
        } else if (GameRules.isDraw(state.getBoard())) {
            state.setStatus(GameStatus.DRAW);
        } else {
            state.setStatus(GameStatus.IN_PROGRESS);
        }

        log.info("Move applied: correlationId={}, gameId={}, moveId={}, moveNumber={}", correlationId, gameId, move.getMoveId(), state.getMoveNumber());

        GameDto dto = toDto(state);
        eventPublisher.publish(state, move, correlationId);
        return dto;
    }

    private void validateMove(GameState state, MoveDto move) {
        if (move.getPlayer() == null)
            throw new ProblemException(HttpStatus.BAD_REQUEST.value(), "Invalid move", "Player is required", INVALID_MOVE);
        if (move.getRow() < 0 || move.getRow() > 2 || move.getCol() < 0 || move.getCol() > 2)
            throw new ProblemException(HttpStatus.BAD_REQUEST.value(), "Invalid move", "Row and col must be 0..2", INVALID_MOVE);
        if (!state.getCell(move.getRow(), move.getCol()).isEmpty())
            throw new ProblemException(HttpStatus.BAD_REQUEST.value(), "Invalid move", "Cell already occupied", INVALID_MOVE);
        PlayerSymbol expected = state.getMoveNumber() % 2 == 0 ? PlayerSymbol.X : PlayerSymbol.O;
        if (move.getPlayer() != expected)
            throw new ProblemException(HttpStatus.BAD_REQUEST.value(), "Invalid move", "Wrong turn: expected " + expected, INVALID_MOVE);
    }

    private GameDto toDto(GameState state) {
        GameDto dto = new GameDto();
        dto.setGameId(state.getGameId());
        BoardDto board = new BoardDto();
        List<List<String>> rows = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            List<String> row = new ArrayList<>();
            for (int c = 0; c < 3; c++) row.add(state.getCell(r, c));
            rows.add(row);
        }
        board.setRows(rows);
        dto.setBoard(board);
        dto.setStatus(state.getStatus());
        dto.setWinner(state.getWinner());
        dto.setMoveNumber(state.getMoveNumber());
        dto.setNextPlayer(state.getStatus() == GameStatus.IN_PROGRESS ? (state.getMoveNumber() % 2 == 0 ? PlayerSymbol.X : PlayerSymbol.O) : null);
        return dto;
    }
}
