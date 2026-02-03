package com.tictactoe.contracts;

import java.util.UUID;

public class GameDto {
    private UUID gameId;
    private BoardDto board;
    private GameStatus status;
    private PlayerSymbol winner;
    private PlayerSymbol nextPlayer;
    private int moveNumber;

    public GameDto() {}

    public UUID getGameId() { return gameId; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }
    public BoardDto getBoard() { return board; }
    public void setBoard(BoardDto board) { this.board = board; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    public PlayerSymbol getWinner() { return winner; }
    public void setWinner(PlayerSymbol winner) { this.winner = winner; }
    public PlayerSymbol getNextPlayer() { return nextPlayer; }
    public void setNextPlayer(PlayerSymbol nextPlayer) { this.nextPlayer = nextPlayer; }
    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }
}
