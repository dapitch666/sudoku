package org.anne.sudoku;

import org.anne.sudoku.solver.MultipleSolutionsFinder;
import org.anne.sudoku.solver.Sudoku;

public class Main {
    public static void main(String[] args) {
        // String input = "46..2..8339.1.6.5..827436.96.325.8......3......9.175.69.456837..7.3.2.6883..7..25";
        String input = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1";
        Sudoku sudoku = new Sudoku(input);
        int solutions = MultipleSolutionsFinder.countSolutions(sudoku);
        System.out.println("Solutions: " + solutions);
    }
}