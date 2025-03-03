package org.anne.sudoku.solver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolverParameterizedTest {

    @DisplayName("Multiple Sudoku puzzles")
    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource({
            "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4., 859612437723854169164379528986147352375268914241593786432981675617425893598736241",
            "..53.....8......2..7..1.5..4....53...1..7...6..32...8..6.5....9..4....3......97.., 145327698839654127672918543496185372218473956753296481367542819984761235521839764",
    })
    void testMainWithParameterizedInput(String input, String expectedSolution) {
        Sudoku sudoku = new Sudoku(input);
        if (Solver.solveSudoku(sudoku)) {
            String solution = Arrays.stream(sudoku.solution).mapToObj(String::valueOf).collect(Collectors.joining());
            assertEquals(expectedSolution, solution);
        }
    }
}
