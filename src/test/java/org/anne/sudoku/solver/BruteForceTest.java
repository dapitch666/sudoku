package org.anne.sudoku.solver;

import org.anne.sudoku.model.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BruteForceTest {

    BruteForce bruteForce = new BruteForce();

    @Test
    void testSolve() {
        Grid grid = new Grid("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");
        String expectedSolution = "859612437723854169164379528986147352375268914241593786432981675617425893598736241";
        assertTrue(bruteForce.solve(grid));
        assertEquals(expectedSolution, grid.getSolution());
    }

    @Test
    void testSolveRandom() {
        Grid grid = new Grid("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");
        String expectedSolution = "859612437723854169164379528986147352375268914241593786432981675617425893598736241";
        assertTrue(bruteForce.solveRandom(grid));
        assertEquals(expectedSolution, grid.getSolution());
    }

    @Test
    void testSolveReverse() {
        Grid grid = new Grid("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");
        String expectedSolution = "859612437723854169164379528986147352375268914241593786432981675617425893598736241";
        assertTrue(bruteForce.solveReverse(grid));
        assertEquals(expectedSolution, grid.getSolution());
    }

    @Test
    void testWithHardestGrid() {
        Grid grid = new Grid("..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1");
        String expectedSolution = "697538124421796835358142679143829756265473918879651342586217493914365287732984561";
        bruteForce.solve(grid);
        assertEquals(expectedSolution, grid.getSolution());
    }

    @Test
    void testWithNoSolution() {
        Grid grid = new Grid("851..24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");
        assertFalse(bruteForce.solve(grid));
    }

    @Test
    void testSolutionGeneration() {
        Grid grid = new Grid();
        bruteForce.solveRandom(grid);
        assertTrue(grid.isSolved());
    }

    @Test
    void testMultipleSolutions() {
        Grid grid = new Grid("46..2..8339.1.6.5..827436.96.325.8......3......9.175.69.456837..7.3.2.6883..7..25");
        assertFalse(bruteForce.hasUniqueSolution(grid));
    }
}