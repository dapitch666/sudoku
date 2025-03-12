package org.anne.sudoku.solver;

import org.anne.sudoku.Utils;

public class MultipleSolutionsFinder {
    static final int N = 9;
    static final int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    // static final String defaultPuzzle = "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.";
    // static final String defaultPuzzle = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1";
    static final String defaultPuzzle = "46..2..8339.1.6.5..827436.96.325.8......3......9.175.69.456837..7.3.2.6883..7..25";

    public static int countSolutions(Sudoku sudoku) {
        return count(sudoku, 0);
    }

    private static int count(Sudoku sudoku, int index) {
        if (index == N * N) {
            return 1;
        }
        if (sudoku.solution[index] != 0) {
            return count(sudoku, index + 1);
        }
        int solutions = 0;
        for (int digit : DIGITS) {
            if (sudoku.isValidMove(index, digit)) {
                sudoku.set(index, digit);
                solutions += count(sudoku, index + 1);
                sudoku.backtrack(index, digit);
                if (solutions > 1) {
                    return solutions; // Early exit if more than one solution is found
                }
            }
        }
        return solutions;
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
        boolean hasMultipleSolutions = countSolutions(sudoku) > 1;
        System.out.println(PrintUtils.printOne(sudoku.puzzle));
        System.out.println(hasMultipleSolutions ? "This puzzle has multiple solutions!" : "This puzzle has a unique solution!");
        System.out.println(timer.duration());
    }
}