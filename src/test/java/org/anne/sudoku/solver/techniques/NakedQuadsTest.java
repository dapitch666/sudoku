package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class NakedQuadsTest {
    @Test
    void nakedQuadsTest() {
        String puzzle = "....3..86....2.........85..371....949.......54....76..2..7..8...3...5...7....4.3.";
        String solved = "142539786587621943693478521371856294968142375425397618214763859839215467756984132";
        runTest(puzzle, solved, new String[]{"Naked Quads", "Hidden Triples"}, new int[]{1, 1}, false);
    }
}