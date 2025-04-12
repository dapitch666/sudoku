package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.ManualSolverTest.runTest;

class XWingsTest {
    @Test
    void xWingsTest() {
        String puzzle = "1.....5694.2.....8.5...9.4....64.8.1....1....2.8.35....4.5...1.9.....4.2621.....5";
        String solved = "187423569492756138356189247539647821764218953218935674843592716975361482621874395";
        runTest(puzzle, solved, new String[]{"X-Wings"}, new int[]{1}, false);
    }

}