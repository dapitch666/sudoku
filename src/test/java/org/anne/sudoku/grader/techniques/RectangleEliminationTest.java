package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.ManualSolverTest.runTest;

class RectangleEliminationTest {
    @Test
    void rectangleElimination() {
        String puzzle = ".3.6.9.2....28....1.......9......65372..6..91365......2.......7....16....1.5.7.4.";
        String solved = "537649128649281735182753469891472653724365891365198274256834917473916582918527346";
        runTest(puzzle, solved, new String[]{"Rectangle Elimination"}, new int[]{6}, false);
    }
}