package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.ManualSolverTest.runTest;

class ChuteRemotePairsTest {
    @Test
    void chuteRemotePairsTest() {
        String puzzle = "...9.5..........12.6.....5.39..5..6....3....4.4..6..85.3.....9.85..1.......2.7...";
        String solved = "128975643975436812463182759397854261586321974241769385732548196859613427614297538";
        runTest(puzzle, solved, new String[]{"Chute Remote Pairs"}, new int[]{2}, false);
    }
}