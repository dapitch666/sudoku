package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.ManualSolverTest.runTest;

class NakedTriplesTest {
    @Test
    void nakedTriplesTest() {
        String puzzle = "...........19..5..56.31..9.1..6...28..4...7..27...4..3.4..68.35..2..59...........";
        String solved = "928547316431986572567312894195673428384251769276894153749168235612435987853729641";
        runTest(puzzle, solved, new String[]{"Naked Pairs", "Naked Triples"}, new int[]{3, 5}, false);
    }
}