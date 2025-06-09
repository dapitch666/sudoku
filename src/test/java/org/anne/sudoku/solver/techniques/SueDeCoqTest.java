package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class SueDeCoqTest {
    @Test
    void sueDeCoqTest1() {
        String puzzle = ".2.....5....4.3...9.6..58..8...2.49...........94.8..61..73..9.6...1.2....5.....3.";
        String solved = "423816759578493612916275843835621497761934285294587361147358926389162574652749138";
        runTest(puzzle, solved, new String[]{"Sue-de-Coq"}, new int[]{1}, true);
    }

    @Test
    void sueDeCoqTest2() {
        String puzzle = "15..32.699...86..5..3........2....4.....9.....3....5........4..3..75...881.24..56";
        String solved = "158432769974186235263975184692517843785394612431628597529863471346751928817249356";
        runTest(puzzle, solved, new String[]{"Sue-de-Coq"}, new int[]{1}, true);
    }
}