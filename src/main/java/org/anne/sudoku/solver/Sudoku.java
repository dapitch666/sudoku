package org.anne.sudoku.solver;

public class Sudoku {
    private static final int N = 9;
    final String puzzle;
    final int[] solution = new int[N * N];
    private final boolean[][] rows = new boolean[N][N + 1];
    private final boolean[][] cols = new boolean[N][N + 1];
    private final boolean[][] squares = new boolean[N][N + 1];

    public Sudoku(String input) {
        puzzle = sanitize(input);
        for (int i = 0; i < N * N; i++) {
            set(i, puzzle.charAt(i) - '0');
        }
    }

    public void set(int index, int digit) {
        solution[index] = digit;
        if (digit != 0) {
            rows[index / N][digit] = true;
            cols[index % N][digit] = true;
            squares[(index / N) / 3 * 3 + (index % N) / 3][digit] = true;
        }
    }

    public void backtrack(int index, int digit) {
        solution[index] = 0;
        rows[index / N][digit] = false;
        cols[index % N][digit] = false;
        squares[(index / N) / 3 * 3 + (index % N) / 3][digit] = false;
    }

    private String sanitize(String input) {
        return String.format("%-" + (N * N) + "s", input)
                .replaceAll("[^0-9]", "0")
                .substring(0, N * N);
    }

    public boolean isValidMove(int index, int digit) {
        return !rows[index / N][digit] && !cols[index % N][digit] && !squares[(index / N) / 3 * 3 + (index % N) / 3][digit];
    }

    private int countCandidates(int index) {
        int count = 0;
        for (int digit = 1; digit <= N; digit++) {
            if (isValidMove(index, digit)) {
                count++;
            }
        }
        return count;
    }

    public int bestIndex() {
        int minCandidates = N + 1;
        int bestIndex = -1;
        for (int i = 0; i < N * N; i++) {
            if (solution[i] == 0) {
                int options = countCandidates(i);
                if (options == 0) return i;
                if (options < minCandidates) {
                    minCandidates = options;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;
    }
}