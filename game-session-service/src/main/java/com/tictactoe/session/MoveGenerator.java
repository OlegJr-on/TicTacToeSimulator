package com.tictactoe.session;

import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.PlayerSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Heuristic: win if possible, block opponent, else center, else corners, else random.
 * Asymmetric suboptimal chance: X (first) blunders more so O can win sometimes too.
 */
public class MoveGenerator {

    private static final Random RANDOM = new Random();
    /** X blunders more often so O gets winning opportunities. */
    private static final double SUBOPTIMAL_CHANCE_X = 0.55;
    private static final double SUBOPTIMAL_CHANCE_O = 0.12;

    public static int[] pickMove(GameDto game, PlayerSymbol player) {
        List<List<String>> rows = game.getBoard().getRows();
        String me = player.name();
        String opp = player == PlayerSymbol.X ? PlayerSymbol.O.name() : PlayerSymbol.X.name();

        List<int[]> empty = collectEmpty(rows);
        if (empty.isEmpty()) return null;

        double suboptimal = player == PlayerSymbol.X ? SUBOPTIMAL_CHANCE_X : SUBOPTIMAL_CHANCE_O;
        if (RANDOM.nextDouble() < suboptimal) return empty.get(RANDOM.nextInt(empty.size()));

        // Optional: randomize first move (e.g. 20% random empty instead of always center)
        if (game.getMoveNumber() == 0 && isEmpty(rows, 1, 1) && RANDOM.nextDouble() < 0.2) {
            if (!empty.isEmpty()) return empty.get(RANDOM.nextInt(empty.size()));
        }

        List<int[]> winMoves = findAllWinningMoves(rows, me);
        if (!winMoves.isEmpty()) return winMoves.get(RANDOM.nextInt(winMoves.size()));

        List<int[]> blockMoves = findAllWinningMoves(rows, opp);
        if (!blockMoves.isEmpty()) return blockMoves.get(RANDOM.nextInt(blockMoves.size()));

        if (isEmpty(rows, 1, 1)) return new int[]{1, 1};

        int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
        List<int[]> emptyCorners = new ArrayList<>();
        for (int[] c : corners) {
            if (isEmpty(rows, c[0], c[1])) emptyCorners.add(new int[]{c[0], c[1]});
        }
        if (!emptyCorners.isEmpty()) return emptyCorners.get(RANDOM.nextInt(emptyCorners.size()));

        return empty.get(RANDOM.nextInt(empty.size()));
    }

    private static List<int[]> collectEmpty(List<List<String>> rows) {
        List<int[]> empty = new ArrayList<>();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (isEmpty(rows, r, c)) empty.add(new int[]{r, c});
        return empty;
    }

    private static boolean isEmpty(List<List<String>> rows, int row, int col) {
        String s = rows.get(row).get(col);
        return s == null || s.isBlank();
    }

    private static List<int[]> findAllWinningMoves(List<List<String>> rows, String symbol) {
        List<int[]> result = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!isEmpty(rows, r, c)) continue;
                if (wouldWin(rows, r, c, symbol)) result.add(new int[]{r, c});
            }
        }
        return result;
    }

    private static boolean wouldWin(List<List<String>> rows, int row, int col, String symbol) {
        int countRow = 0, countCol = 0, countD1 = 0, countD2 = 0;
        for (int i = 0; i < 3; i++) {
            if (symbol.equals(get(rows, row, i))) countRow++;
            if (symbol.equals(get(rows, i, col))) countCol++;
            if (symbol.equals(get(rows, i, i))) countD1++;
            if (symbol.equals(get(rows, i, 2 - i))) countD2++;
        }
        boolean rowWin = countRow == 2;
        boolean colWin = countCol == 2;
        boolean d1Win = (row == col) && countD1 == 2;
        boolean d2Win = (row + col == 2) && countD2 == 2;
        return rowWin || colWin || d1Win || d2Win;
    }

    private static String get(List<List<String>> rows, int r, int c) {
        String s = rows.get(r).get(c);
        return s == null ? "" : s.trim();
    }
}
