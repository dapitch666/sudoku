package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

public class SimpleColoringTest {
    @Test
    void simpleColoringTest1() {
        String puzzle = ".......6...27.5...5...13..97.45....3..3.4.1..9....74.56..92...4...3.18...8.......";
        String solved = "371294568892765341546813279714582693253649187968137425635928714429371856187456932";
        runTest(puzzle, solved, new String[]{"Simple Coloring"}, new int[]{2}, false);
    }

    @Test
    void simpleColoringTest2() {
        String puzzle = "1..4....6.46.91.8...5.2.......5..1.9.9.....5.4.2..9.......1.9...8.93.56.5....8..4";
        String solved = "128453796346791285975826413763582149891347652452169837634215978287934561519678324";
        runTest(puzzle, solved, new String[]{"Simple Coloring"}, new int[]{1}, false);
    }

    @Test
    void simpleColoringTest3() {
        String puzzle = "...9.6...6....8..71..37...9..6...75...4.3.1...95...8..2...65..89..8....5...1.3...";
        String solved = "437956281659218437182374569316489752824537196795621843243765918961842375578193624";
        runTest(puzzle, solved, new String[]{"Simple Coloring"}, new int[]{2}, false);
    }

    @Test
    void simpleColoringTest4() {
        String puzzle = "4..8....3..6.1.4.9.....5....1..6..92...3.1...64..5..8....6.....9.7.8.1..8....9..4";
        String solved = "495876213786213459321945867513468792278391645649752381154627938937584126862139574";
        runTest(puzzle, solved, new String[]{"Simple Coloring"}, new int[]{2}, false);
    }


}
