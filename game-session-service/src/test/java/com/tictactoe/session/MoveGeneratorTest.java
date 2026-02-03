package com.tictactoe.session;

import com.tictactoe.contracts.BoardDto;
import com.tictactoe.contracts.GameDto;
import com.tictactoe.contracts.PlayerSymbol;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {

    @Test
    void pickWinMove() {
        GameDto game = gameWithBoard(
            "X", "X", "",
            "", "O", "",
            "", "", ""
        );
        // With SUBOPTIMAL_CHANCE we may get a random empty cell; otherwise we get winning (0,2)
        int[] move = MoveGenerator.pickMove(game, PlayerSymbol.X);
        assertNotNull(move);
        assertEquals(2, move.length);
        assertTrue(move[0] >= 0 && move[0] <= 2 && move[1] >= 0 && move[1] <= 2);
        List<int[]> empty = List.of(new int[]{0, 2}, new int[]{1, 0}, new int[]{1, 2}, new int[]{2, 0}, new int[]{2, 1}, new int[]{2, 2});
        assertTrue(empty.stream().anyMatch(c -> c[0] == move[0] && c[1] == move[1]), "Move must be an empty cell");
    }

    @Test
    void pickBlockMove() {
        GameDto game = gameWithBoard(
            "O", "O", "",
            "", "X", "",
            "", "", ""
        );
        int[] move = MoveGenerator.pickMove(game, PlayerSymbol.X);
        assertNotNull(move);
        assertEquals(2, move.length);
        assertTrue(move[0] >= 0 && move[0] <= 2 && move[1] >= 0 && move[1] <= 2);
        List<int[]> empty = List.of(new int[]{0, 2}, new int[]{1, 0}, new int[]{1, 2}, new int[]{2, 0}, new int[]{2, 1}, new int[]{2, 2});
        assertTrue(empty.stream().anyMatch(c -> c[0] == move[0] && c[1] == move[1]), "Move must be an empty cell");
    }

    @Test
    void cornerChoiceVariesWhenMultipleCornersEmpty() {
        // Board: only center taken, so O has 4 empty corners. Over many runs we should see more than one corner.
        GameDto game = gameWithBoard(
            "", "", "",
            "", "X", "",
            "", "", ""
        );
        game.setMoveNumber(1);
        game.setNextPlayer(PlayerSymbol.O);
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int i = 0; i < 50; i++) {
            int[] move = MoveGenerator.pickMove(game, PlayerSymbol.O);
            assertNotNull(move);
            seen.add(move[0] + "," + move[1]);
        }
        assertTrue(seen.size() > 1, "Corner choice should vary; saw " + seen.size() + " distinct moves over 50 runs");
    }

    @Test
    void pickValidMoveWhenEmpty() {
        GameDto game = gameWithBoard(
            "", "", "",
            "", "", "",
            "", "", ""
        );
        int[] move = MoveGenerator.pickMove(game, PlayerSymbol.X);
        assertNotNull(move);
        assertEquals(2, move.length);
        assertTrue(move[0] >= 0 && move[0] <= 2 && move[1] >= 0 && move[1] <= 2);
    }

    private static GameDto gameWithBoard(String... cells) {
        GameDto dto = new GameDto();
        BoardDto board = new BoardDto();
        List<List<String>> rows = Arrays.asList(
            Arrays.asList(cells[0], cells[1], cells[2]),
            Arrays.asList(cells[3], cells[4], cells[5]),
            Arrays.asList(cells[6], cells[7], cells[8])
        );
        board.setRows(rows);
        dto.setBoard(board);
        dto.setNextPlayer(PlayerSymbol.X);
        return dto;
    }
}
