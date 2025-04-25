package org.anne.sudoku.solver;

import org.anne.sudoku.model.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SolutionCounterTest {
    @Test
    void testMultipleSolutions() {
        BruteForce bruteForce = new BruteForce();
        Grid grid = new Grid("46..2..8339.1.6.5..827436.96.325.8......3......9.175.69.456837..7.3.2.6883..7..25");
        assertFalse(bruteForce.hasUniqueSolution(grid));
    }
}