package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class IntersectionRemovalTest {
    @Test
    void intersectionRemovalTest1() {
        String puzzle = ".1.9.36......8....9.....5.7..2.1.43....4.2....64.7.2..7.1.....5....3......56.1.2.";
        String solved = "417953682256187943983246517872519436539462871164378259791824365628735194345691728";
        runTest(puzzle, solved, new String[]{"Intersection Removal"}, new int[]{9}, false);
    }

    @Test
    void intersectionRemovalTest2() {
        String puzzle = ".16..78.3...8......7...1.6..48...3..6.......2..9...65..6.9...2......2...9.46..51.";
        String solved = "416527893592836147873491265148265379657319482239784651361958724785142936924673518";
        runTest(puzzle, solved, new String[]{"Intersection Removal"}, new int[]{7}, false);
    }

    @Test
    void intersectionRemovalTestTriple() {
        String puzzle = "...921..3..9....6.......5...8.4.3..6..7...8..5..7...4...3.......2....7..8..195...";
        String solved = "765921483319548267248367519182453976437619825596782341653274198921836754874195632";
        runTest(puzzle, solved, new String[]{"Intersection Removal"}, new int[]{10}, true);
    }
}