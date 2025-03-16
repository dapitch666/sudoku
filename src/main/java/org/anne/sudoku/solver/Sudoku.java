package org.anne.sudoku.solver;

import org.anne.sudoku.Grade;
import org.anne.sudoku.Utils;
import org.anne.sudoku.crawler.SudokuWiki;

import static org.anne.sudoku.Constants.DIGITS;

public class Sudoku {
    public static final int N = 9;
    public final String puzzle;
    private String solution;
    private Grade grade;
    public final int[] grid = new int[N * N];
    private final boolean[][] rows = new boolean[N][N + 1];
    private final boolean[][] cols = new boolean[N][N + 1];
    private final boolean[][] squares = new boolean[N][N + 1];

    public Sudoku(String input) {
        puzzle = sanitize(input);
        for (int i = 0; i < N * N; i++) {
            set(i, puzzle.charAt(i) - '0');
        }
        // if (solve()) solution = Utils.arrayToString(grid);
    }

    public void set(int index, int digit) {
        grid[index] = digit;
        if (digit != 0) {
            rows[index / N][digit] = true;
            cols[index % N][digit] = true;
            squares[(index / N) / 3 * 3 + (index % N) / 3][digit] = true;
        }
    }

    public Grade getGrade() {
        if (grade == null) {
            grade = new SudokuWiki().getGrade(puzzle);
        }
        return grade;
    }

    public String getSolution() {
        if (solution == null) {
            solve();
            solution = Utils.arrayToString(grid);
        }
        return solution;
    }

    public boolean solve() {
        int index = bestIndex();
        if (index == -1) return true; // All cells are filled, sudoku is solved

        for (int digit : DIGITS) {
            if (isValidMove(index, digit)) {
                set(index, digit);
                if (solve()) return true;
                backtrack(index, digit);
            }
        }
        return false;
    }

    public int get(int index) {
        return grid[index];
    }

    public void backtrack(int index, int digit) {
        grid[index] = 0;
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
            if (grid[i] == 0) {
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