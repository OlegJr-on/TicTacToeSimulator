package com.tictactoe.engine;

import com.tictactoe.contracts.GameStatus;
import com.tictactoe.contracts.PlayerSymbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {

    @Test
    void rowWin() {
        String[][] board = new String[3][3];
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) board[r][c] = "";
        board[1][0] = "X";
        board[1][1] = "X";
        board[1][2] = "X";
        assertEquals(GameStatus.WIN, GameRules.checkWin(board));
        assertEquals(PlayerSymbol.X, GameRules.getWinner(board));
    }

    @Test
    void colWin() {
        String[][] board = new String[3][3];
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) board[r][c] = "";
        board[0][2] = "O";
        board[1][2] = "O";
        board[2][2] = "O";
        assertEquals(GameStatus.WIN, GameRules.checkWin(board));
        assertEquals(PlayerSymbol.O, GameRules.getWinner(board));
    }

    @Test
    void diagWin() {
        String[][] board = new String[3][3];
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) board[r][c] = "";
        board[0][0] = "X";
        board[1][1] = "X";
        board[2][2] = "X";
        assertEquals(GameStatus.WIN, GameRules.checkWin(board));
        assertEquals(PlayerSymbol.X, GameRules.getWinner(board));
    }

    @Test
    void antiDiagWin() {
        String[][] board = new String[3][3];
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) board[r][c] = "";
        board[0][2] = "O";
        board[1][1] = "O";
        board[2][0] = "O";
        assertEquals(GameStatus.WIN, GameRules.checkWin(board));
        assertEquals(PlayerSymbol.O, GameRules.getWinner(board));
    }

    @Test
    void draw() {
        String[][] board = {
            {"X", "O", "X"},
            {"X", "O", "O"},
            {"O", "X", "X"}
        };
        assertEquals(GameStatus.IN_PROGRESS, GameRules.checkWin(board));
        assertTrue(GameRules.isDraw(board));
    }

    @Test
    void inProgress() {
        String[][] board = new String[3][3];
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) board[r][c] = "";
        board[0][0] = "X";
        board[1][1] = "O";
        assertEquals(GameStatus.IN_PROGRESS, GameRules.checkWin(board));
        assertFalse(GameRules.isDraw(board));
    }
}
