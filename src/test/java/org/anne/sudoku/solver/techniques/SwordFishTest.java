package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

public class SwordFishTest {
    @Test
    void swordfishTest1() {
        String puzzle = "1.73...4.8....6....5.87.63..9....51.........77...6..8....9.4....8.1....241.......";
        String solved = "167359248823416759954872631298743516641598327735261984372984165586137492419625873";
        runTest(puzzle, solved, new String[]{"Sword Fish"}, new int[]{1}, false);
    }

    @Test
    void swordfishTest2() {
        String puzzle = "3...4.........7.48......9.7.1...3.8.4...5..2..5...8.7.5..3............9.6.9.253..";
        String solved = "387942651195637248264581937712493586438756129956218473541379862823164795679825314";
        runTest(puzzle, solved, new String[]{"Sword Fish"}, new int[]{2}, false);
    }

    @Test
    void swordfishTest3() {
        String puzzle = "43...8......4...2..26..51.8...9......946.275......3...3.51..9...7...9......5...12";
        String solved = "437218569518496327926375148763951284194682753852743691385124976271869435649537812";
        runTest(puzzle, solved, new String[]{"Sword Fish"}, new int[]{1}, false);
    }
}
