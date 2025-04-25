package org.anne.sudoku.solver.techniques;


import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class HiddenTriplesTest {
    @Test
    void hiddenTriplesTest() {
        String puzzle = "3........97..1....6..583...2.....9..5..621..3..8.....5...435..2....9..56........1";
        String solved = "381976524975214638642583179264358917597621483138749265816435792423197856759862341";
        runTest(puzzle, solved, new String[]{"Hidden Triples", "Naked Triples"}, new int[]{1, 5}, false);
    }
}