package com.tictactoe.engine;

import com.tictactoe.contracts.GameStatus;
import com.tictactoe.contracts.PlayerSymbol;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private final UUID gameId;
    private final String[][] board = new String[3][3];
    private GameStatus status = GameStatus.IN_PROGRESS;
    private PlayerSymbol winner;
    private int moveNumber = 0;
    private final Set<UUID> appliedMoveIds = ConcurrentHashMap.newKeySet();

    public GameState(UUID gameId) {
        this.gameId = gameId;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = "";
    }

    public UUID getGameId() { return gameId; }
    public String[][] getBoard() { return board; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    public PlayerSymbol getWinner() { return winner; }
    public void setWinner(PlayerSymbol winner) { this.winner = winner; }
    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }
    public Set<UUID> getAppliedMoveIds() { return appliedMoveIds; }

    public String getCell(int row, int col) { return board[row][col]; }
    public void setCell(int row, int col, String symbol) { board[row][col] = symbol; }
}
