package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class FinnedXWingsTest {
    @Test
    void finnedXWingsTest1() {
        String puzzle = "7....8.3..36..........5.84.2.59........745........36.4.29.1..........91..1.5....8";
        String solved = "754198236836472591192356847245961783368745129971283654429817365587634912613529478";
        runTest(puzzle, solved, new String[]{"Finned X-Wings"}, new int[]{1}, false);
    }

    @Test
    void finnedXWingsTest2() {
        String puzzle = "9...4....7.4.8..5..8....1....76..82.62.4............19...1.2...89.7.........5...3";
        String solved = "951246387764381952283975146517693824629418735438527619375162498892734561146859273";
        runTest(puzzle, solved, new String[]{"Finned X-Wings"}, new int[]{2}, false);
    }
}