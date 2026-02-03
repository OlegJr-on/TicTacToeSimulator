package com.tictactoe.engine;

import com.tictactoe.contracts.GameStatus;
import com.tictactoe.contracts.PlayerSymbol;

public final class GameRules {

    private GameRules() {}

    public static GameStatus checkWin(String[][] board) {
        for (int i = 0; i < 3; i++) {
            if (isRowWin(board, i)) return GameStatus.WIN;
            if (isColWin(board, i)) return GameStatus.WIN;
        }
        if (isDiagWin(board)) return GameStatus.WIN;
        return GameStatus.IN_PROGRESS;
    }

    public static PlayerSymbol getWinner(String[][] board) {
        for (int i = 0; i < 3; i++) {
            String r = rowWinner(board, i);
            if (r != null) return PlayerSymbol.valueOf(r);
            String c = colWinner(board, i);
            if (c != null) return PlayerSymbol.valueOf(c);
        }
        String d = diagWinner(board);
        if (d != null) return PlayerSymbol.valueOf(d);
        return null;
    }

    public static boolean isDraw(String[][] board) {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c].isEmpty()) return false;
        return true;
    }

    private static boolean isRowWin(String[][] board, int row) {
        String a = board[row][0], b = board[row][1], c = board[row][2];
        return !a.isEmpty() && a.equals(b) && b.equals(c);
    }

    private static boolean isColWin(String[][] board, int col) {
        String a = board[0][col], b = board[1][col], c = board[2][col];
        return !a.isEmpty() && a.equals(b) && b.equals(c);
    }

    private static boolean isDiagWin(String[][] board) {
        String a = board[0][0], b = board[1][1], c = board[2][2];
        if (!a.isEmpty() && a.equals(b) && b.equals(c)) return true;
        a = board[0][2]; b = board[1][1]; c = board[2][0];
        return !a.isEmpty() && a.equals(b) && b.equals(c);
    }

    private static String rowWinner(String[][] board, int row) {
        String a = board[row][0], b = board[row][1], c = board[row][2];
        return (!a.isEmpty() && a.equals(b) && b.equals(c)) ? a : null;
    }

    private static String colWinner(String[][] board, int col) {
        String a = board[0][col], b = board[1][col], c = board[2][col];
        return (!a.isEmpty() && a.equals(b) && b.equals(c)) ? a : null;
    }

    private static String diagWinner(String[][] board) {
        String a = board[0][0], b = board[1][1], c = board[2][2];
        if (!a.isEmpty() && a.equals(b) && b.equals(c)) return a;
        a = board[0][2]; b = board[1][1]; c = board[2][0];
        return (!a.isEmpty() && a.equals(b) && b.equals(c)) ? a : null;
    }
}
