package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class SubsetExclusionTest {
    @Test
    void subsetExclusionTest1() {
        String puzzle = ".........6..598..4...7..52..9...2.4.2.1...8.6.7.8...5..62..7...8..634..9.........";
        String solved = "754123968623598714189746523598362147231475896476819352362957481815634279947281635";
        runTest(puzzle, solved, new String[]{"Aligned Pair Exclusion"}, new int[]{1}, false);
    }

    @Test
    void subsetExclusionTest2() {
        String puzzle = "...138...5.39..4..7...5.....79..2.1...........8.5..96.....9...3..6..31.7...641...";
        String solved = "694138275523976481718254396479362518165489732382517964841795623956823147237641859";
        runTest(puzzle, solved, new String[]{"Aligned Pair Exclusion"}, new int[]{5}, false);
    }

    @Test
    void subsetExclusionTest3() {
        String puzzle = "......1.6..259....3.8.4.....8.....7....2.4....9..7..4.....5.6.3....389..1.5......";
        String solved = "954783126762591438318642597481369275573214869296875341829457613647138952135926784";
        runTest(puzzle, solved, new String[]{"Aligned Pair Exclusion"}, new int[]{2}, false);
    }
}