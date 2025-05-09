package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class ChuteRemotePairsTest {
    @Test
    void chuteRemotePairsTest1() {
        String puzzle = "...9.5..........12.6.....5.39..5..6....3....4.4..6..85.3.....9.85..1.......2.7...";
        String solved = "128975643975436812463182759397854261586321974241769385732548196859613427614297538";
        runTest(puzzle, solved, new String[]{"Chute Remote Pairs"}, new int[]{2}, false);
    }

    @Test
    void chuteRemotePairsTest2() {
        String puzzle = ".5.....1....573....28..13..6..2......1.....5.....68..4..58..79.....96....3.....8.";
        String solved = "354682917196573842728941365643259178812437659579168234265814793487396521931725486";
        runTest(puzzle, solved, new String[]{"Chute Remote Pairs"}, new int[]{2}, false);
    }

    @Test
    void chuteRemotePairsDoubleEliminationTest() {
        String puzzle = "...3.6...36....19...81......2...9.8.9.......5.7.4...6......26...49....72...8.3...";
        String solved = "415396827362785194798124536624519783983267415571438269137942658849651372256873941";
        runTest(puzzle, solved, new String[]{"Chute Remote Pairs"}, new int[]{2}, false);
    }
}