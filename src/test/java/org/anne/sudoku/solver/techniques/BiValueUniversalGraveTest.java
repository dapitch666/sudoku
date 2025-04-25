package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class BiValueUniversalGraveTest {
    @Test
    void biValueUniversalGraveTest1() {
        String puzzle = "..1...7.6736.....55......82....78......52.......139...392...5..6.....137.5....4..";
        String solved = "821953746736842915549761382415678293963524871278139654392417568684295137157386429";
        runTest(puzzle, solved, new String[]{"BiValue Universal Grave"}, new int[]{1}, false);
    }

    @Test
    void biValueUniversalGraveTest2() {
        String puzzle = "2..4..5.1..1.38.9..3....7.8.7...2..3.6..9...5.4......9..4....6.62.3..8..81..47...";
        String solved = "289476531751238496436915728975182643362794185148563279594821367627359814813647952";
        runTest(puzzle, solved, new String[]{"BiValue Universal Grave"}, new int[]{1}, false);
    }
}