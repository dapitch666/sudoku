package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class HiddenSinglesTest {
    @Test
    void hiddenSinglesTest() {
        String puzzle = ".....4.284.6.....51...3.6.....3.1....87...14....7.9.....2.1...39.....5.767.4.....";
        String solved = "735164928426978315198532674249381756387256149561749832852617493914823567673495281";
        runTest(puzzle, solved, new String[]{"Hidden Singles"}, new int[]{15}, false);
    }
}