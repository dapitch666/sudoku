package org.anne.sudoku.solver;

import java.util.BitSet;

public class Sudoku {
    private static final int N = 9;
    final String puzzle;
    final int[] solution = new int[N * N];
    private final BitSet[] rows = new BitSet[N];
    private final BitSet[] cols = new BitSet[N];
    private final BitSet[] squares = new BitSet[N];

    public Sudoku(String input) {
        puzzle = sanitize(input);
        for (int i = 0; i < N; i++) {
            rows[i] = new BitSet(N + 1);
            cols[i] = new BitSet(N + 1);
            squares[i] = new BitSet(N + 1);
        }
        for (int i = 0; i < N * N; i++) {
            set(i, puzzle.charAt(i) - '0');
        }
    }

    public void set(int index, int digit) {
        solution[index] = digit;
        if (digit != 0) {
            row(index).set(digit);
            column(index).set(digit);
            square(index).set(digit);
        }
    }

    public void backtrack(int index, int digit) {
        solution[index] = 0;
        row(index).clear(digit);
        column(index).clear(digit);
        square(index).clear(digit);
    }

    private String sanitize(String input) {
        return String.format("%-" + (N * N) + "s", input)
                .replaceAll("[^0-9]", "0")
                .substring(0, N * N);
    }

    public boolean isValidMove(int index, int digit) {
        return !row(index).get(digit) && !column(index).get(digit) && !square(index).get(digit);
    }

    private BitSet row(int i) {
        return rows[i / N];
    }

    private BitSet column(int i) {
        return cols[i % N];
    }

    private BitSet square(int i) {
        return squares[(i / N) / 3 * 3 + (i % N) / 3];
    }
}
