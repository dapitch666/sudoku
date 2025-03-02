package org.anne.sudoku.solver;

import java.util.BitSet;

public class Solver {
    static final int N = 9;
    static final int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    static int backtrackCount = 0;
    static int[] grid = new int[N * N];
    static final String defaultPuzzle = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1";

    static boolean isValid(int index, int digit, BitSet[] rows, BitSet[] columns, BitSet[] squares) {
        return !row(rows, index).get(digit) && !column(columns, index).get(digit) && !square(squares, index).get(digit);
    }

    static BitSet row(BitSet[] rows, int i) {
        return rows[i / N];
    }

    static BitSet column(BitSet[] columns, int i) {
        return columns[i % N];
    }

    static BitSet square(BitSet[] squares, int i) {
        return squares[(i / N) / 3 * 3 + (i % N) / 3];
    }

    static boolean sudokuSolverRec(int[] grid, int index, BitSet[] rows, BitSet[] columns, BitSet[] squares) {
        if (index == N * N)
            return true;

        if (grid[index] != 0)
            return sudokuSolverRec(grid, index + 1, rows, columns, squares);

        for (int digit : DIGITS) {
            if (isValid(index, digit, rows, columns, squares)) {
                grid[index] = digit;
                row(rows, index).set(digit);
                column(columns, index).set(digit);
                square(squares, index).set(digit);

                if (sudokuSolverRec(grid, index + 1, rows, columns, squares))
                    return true;

                grid[index] = 0;
                row(rows, index).clear(digit);
                column(columns, index).clear(digit);
                square(squares, index).clear(digit);
            }
        }

        backtrackCount++;
        return false;
    }

    static void solveSudoku(String puzzle) {
        BitSet[] rows = new BitSet[N];
        BitSet[] cols = new BitSet[N];
        BitSet[] squares = new BitSet[N];

        for (int i = 0; i < N; i++) {
            rows[i] = new BitSet(10);
            cols[i] = new BitSet(10);
            squares[i] = new BitSet(10);
        }

        for (int i = 0; i < N * N; i++) {
            int r = i / 9;
            int c = i % 9;
            int n = puzzle.charAt(i) - '0';
            grid[i] = n;
            if (n != 0) {
                rows[r].set(n);
                cols[c].set(n);
                squares[(r / 3) * 3 + c / 3].set(n);
            }
        }

        sudokuSolverRec(grid, 0, rows, cols, squares);
    }

    public static void main(String[] args) {
        String puzzle = String.format("%-" + (81) + "s", args.length == 0 ? defaultPuzzle : args[0])
                .replaceAll("[^0-9]", "0")
                .substring(0, 81);

        long start = System.nanoTime();

        solveSudoku(puzzle);

        long elapsed = System.nanoTime() - start;

        Printer.print(puzzle, grid);
        System.out.println("Executed in " + elapsed / 1000000 + "ms");
        System.out.println("Number of backtracks: " + backtrackCount);
    }
}
