package org.anne.sudoku.solver;


import org.anne.sudoku.Utils;

public class Solver {
    static final int N = 9;
    static final int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    static int backtrackCount = 0;
    // static final String defaultPuzzle = "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.";
    static final String defaultPuzzle = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1";
    // static final String defaultPuzzle = "632.7..9.5.13.2......8.....76...3.81.........95.7...36.....7......6.48.2.4..8.56.";

    static boolean solveSudoku(Sudoku sudoku) {
        int index = sudoku.bestIndex();
        if (index == -1) return true; // all cells are filled

        for (int digit : DIGITS) {
            if (sudoku.isValidMove(index, digit)) {
                sudoku.set(index, digit);
                if (solveSudoku(sudoku)) return true;
                sudoku.backtrack(index, digit);
            }
        }
        backtrackCount++;
        return false;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        Sudoku sudoku = new Sudoku(args.length == 0 ? defaultPuzzle : args[0]);
        if (!Utils.isValidPuzzle(sudoku.puzzle)) {
            System.out.println("Invalid input!");
            return;
        }
        if (Utils.isAlreadySolved(sudoku.puzzle)) {
            System.out.println("Puzzle is already solved!");
            return;
        }
        if (!solveSudoku(sudoku)) {
            System.out.println("No solution found!");
            System.out.println(PrintUtils.printOne(sudoku.puzzle));
            return;
        }
        System.out.println(PrintUtils.printBoth(sudoku.puzzle, sudoku.solution));
        System.out.println(timer.duration());
        System.out.println("Number of backtracks: " + backtrackCount);
    }
}