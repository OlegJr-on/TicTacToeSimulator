package com.tictactoe.contracts;

import java.util.ArrayList;
import java.util.List;

/**
 * Board representation: List of 3 rows, each row is List of 3 strings ("" for empty, "X" or "O" for filled).
 */
public class BoardDto {
    private List<List<String>> rows = new ArrayList<>();

    public BoardDto() {
        for (int r = 0; r < 3; r++) {
            List<String> row = new ArrayList<>();
            for (int c = 0; c < 3; c++) row.add("");
            rows.add(row);
        }
    }

    public List<List<String>> getRows() { return rows; }
    public void setRows(List<List<String>> rows) { this.rows = rows != null ? rows : new ArrayList<>(); }
}
