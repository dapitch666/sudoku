package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class SueDeCoqTest {
    @Test
    void sueDeCoqTest() {
        String puzzle = "15..32.699...86..5..3........2....4.....9.....3....5........4..3..75...881.24..56";
        String solved = "158432769974186235263975184692517843785394612431628597529863471346751928817249356";
        runTest(puzzle, solved, new String[]{"Sue-de-Coq"}, new int[]{1}, false);
    }
}