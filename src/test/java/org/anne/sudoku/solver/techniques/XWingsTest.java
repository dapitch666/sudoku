package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class XWingsTest {
    @Test
    void xWingsTest1() {
        String puzzle = "1.....5694.2.....8.5...9.4....64.8.1....1....2.8.35....4.5...1.9.....4.2621.....5";
        String solved = "187423569492756138356189247539647821764218953218935674843592716975361482621874395";
        runTest(puzzle, solved, new String[]{"X-Wings"}, new int[]{1}, false);
    }

    @Test
    void xWingsTest2() {
        String puzzle = "..7.1.......8..5..18...9.646.......3.71.8.64.4.......584.6...31..5..2.......3.7..";
        String solved = "567314928394826517182759364658247193971583642423961875849675231735192486216438759";
        runTest(puzzle, solved, new String[]{"X-Wings"}, new int[]{3}, false);
    }

}