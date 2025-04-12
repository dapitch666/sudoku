package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.ManualSolverTest.runTest;

class NakedSinglesTest {
    @Test
    void nakedSinglesTest() {
        String puzzle = "3..967..1.4.3.2.8..2.....7..7.....9....873...5...1...3..47.51..9.5...2.78..621..4";
        String solved = "358967421741352689629184375173546892492873516586219743264795138915438267837621954";
        runTest(puzzle, solved, new String[]{"Naked Singles"}, new int[]{49}, false);
    }

}