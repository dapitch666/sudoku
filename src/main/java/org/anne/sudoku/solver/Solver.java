package org.anne.sudoku.solver;

import org.anne.sudoku.Grade;
import org.anne.sudoku.Utils;
import org.anne.sudoku.crawler.SudokuWiki;

import static org.anne.sudoku.Constants.DIGITS;

public class Solver {
    public static final int N = 9;
    public final String puzzle;
    private String solution;
    public final int[] grid = new int[N * N];
    private final boolean[][] rows = new boolean[N][N + 1];
    private final boolean[][] cols = new boolean[N][N + 1];
    private final boolean[][] squares = new boolean[N][N + 1];

    static int backtrackCount = 0;
    // static final String defaultPuzzle = "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.";
    // static final String defaultPuzzle = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1";
    static final String defaultPuzzle = "5.4.6.......2...8...81....9.395...67....7.91.47...1.....13....5.....92.3.8.7.....";
    // static final String defaultPuzzle = "632.7..9.5.13.2......8.....76...3.81.........95.7...36.....7......6.48.2.4..8.56.";

    public Solver(String input) {
        puzzle = sanitize(input);
        for (int i = 0; i < N * N; i++) {
            set(i, puzzle.charAt(i) - '0');
        }
    }

    public void set(int index, int digit) {
        grid[index] = digit;
        if (digit != 0) {
            rows[index / N][digit] = true;
            cols[index % N][digit] = true;
            squares[(index / N) / 3 * 3 + (index % N) / 3][digit] = true;
        }
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
        backtrackCount++;
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

    public static void main(String[] args) {
        Timer timer = new Timer();
        Solver solver = new Solver(args.length == 0 ? defaultPuzzle : args[0]);
        if (!Utils.isValidPuzzle(solver.puzzle)) {
            System.out.println("Invalid input!");
            return;
        }
        if (Utils.isAlreadySolved(solver.puzzle)) {
            System.out.println("Puzzle is already solved!");
            return;
        }
        if (!solver.solve()) {
            System.out.println("No solution found!");
            System.out.println(PrintUtils.printOne(solver.puzzle));
            return;
        }
        System.out.println(PrintUtils.printBoth(solver.puzzle, solver.grid));
        System.out.println(timer.duration());
        System.out.println("Number of backtracks: " + backtrackCount);
    }
}