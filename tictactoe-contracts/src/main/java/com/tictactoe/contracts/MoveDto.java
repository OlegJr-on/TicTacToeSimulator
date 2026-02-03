package com.tictactoe.contracts;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class MoveDto {
    @NotNull
    private java.util.UUID moveId;
    @NotNull
    private PlayerSymbol player;
    @Min(0)
    @Max(2)
    private int row;
    @Min(0)
    @Max(2)
    private int col;
    private Instant timestamp;

    public MoveDto() {}

    public MoveDto(java.util.UUID moveId, PlayerSymbol player, int row, int col, Instant timestamp) {
        this.moveId = moveId;
        this.player = player;
        this.row = row;
        this.col = col;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public java.util.UUID getMoveId() { return moveId; }
    public void setMoveId(java.util.UUID moveId) { this.moveId = moveId; }
    public PlayerSymbol getPlayer() { return player; }
    public void setPlayer(PlayerSymbol player) { this.player = player; }
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
