package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class FireworksTest {
    @Test
    void fireworksTest() {
        String puzzle = ".45.........1...7.8...23......9.71........3...8.4.6.2...3.....5.7.8....6......9..";
        String solved = "245798613936154872817623459352987164764512398189436527493261785571849236628375941";
        runTest(puzzle, solved, new String[]{"Fireworks"}, new int[]{1}, false);
    }

    @Disabled // Need Extended Unique Rectangle, Aligned Pair Exclusion and Alternating Infer. Chains
    @Test
    void fireworksTest2() {
        String puzzle = "23...8..5.6.2.........9.1....6...32.4.3...5.1.25...9....7.8.........2.7.1..9...58";
        String solved = "239418765761253894584796132916845327473629581825371946697584213358162479142937658";
        runTest(puzzle, solved, new String[]{"Fireworks"}, new int[]{1}, true);
    }

    @Test
    void fireworksTest3() {
        String puzzle = "..4..8..7....6....5..1...28...4...96....7..8.3.....21.1....347..27.......5..1....";
        String solved = "264398157781562349539147628812435796946271583375986214198623475627854931453719862";
        runTest(puzzle, solved, new String[]{"Fireworks"}, new int[]{2}, true);
    }

    @Test
    void fireworksTest4() {
        String puzzle = "5....7.38...9.8..2.7....15.34.1...........8..2...6......36....9.....9.4.7.6......";
        String solved = "562417938134958672978236154349182567615794823287365491453621789821579346796843215";
        runTest(puzzle, solved, new String[]{"Fireworks"}, new int[]{1}, true);
    }

    @Test
    void fireworksTest5() {
        String puzzle = "1.....4......1.....98.....6...42....7....53....2.867..9.......53....2...47.6.....";
        String solved = "153269478647813259298574136839427561764195382512386794926731845385942617471658923";
        runTest(puzzle, solved, new String[]{"Fireworks"}, new int[]{2}, true);
    }
}
