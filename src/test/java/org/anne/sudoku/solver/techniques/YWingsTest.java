package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class YWingsTest {
    @Test
    void yWingsTest() {
        String puzzle = "9...4.......6...31.2.....9....7...2...29356...7...2....6.....7351...9.......8...9";
        String solved = "931247586754698231628153794195764328482935617376812945869521473513479862247386159";
        runTest(puzzle, solved, new String[]{"Y-Wings"}, new int[]{2}, true);
    }
}