package org.anne.sudoku.solver.techniques;


import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class HiddenQuadsTest {
    @Test
    void hiddenQuadsTest() {
        String puzzle = "...7.5..6....4..81....3..5..41.....8.6.....2.5.....43.....7....978.5....3..2.1...";
        String solved = "482715396635942781197638254741523968863497125529186437216879543978354612354261879";
        runTest(puzzle, solved, new String[]{"Hidden Quads"}, new int[]{1}, false);
    }
}