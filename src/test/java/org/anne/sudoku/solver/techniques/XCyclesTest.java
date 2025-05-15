package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

public class XCyclesTest {
    @Test
    void xCyclesTest1() {
        String puzzle = ".........89.632..4..2.9.8...7....6..9....5..8..1....3...3.1.2..6..873.19.........";
        String solved = "365481792897632154412597863578349621936125478241768935753914286624873519189256347";
        runTest(puzzle, solved, new String[]{"X-Cycles"}, new int[]{5}, false);
    }

    @Test
    void xCyclesTest2() {
        String puzzle = ".......2...5.8.4..4..1..8...9...2....37...56....97......4..86.5..6.4.7...8.......";
        String solved = "819457326725683491463129857698532174237814569541976238374298615956341782182765943";
        runTest(puzzle, solved, new String[]{"X-Cycles", "XY-Chains"}, new int[]{3, 1}, false);
    }

    @Test
    void xCyclesTest3() {
        String puzzle = ".4...58..7...1.9....3..71..4..7......5.9.8.4......2..8..95..7......2...5..41...9.";
        String solved = "142695873765813924893247156428751639351968247976432518239584761617329485584176392";
        runTest(puzzle, solved, new String[]{"X-Cycles", "XY-Chains"}, new int[]{1, 1}, false);
    }

    @Test
    void xCyclesTest4() {
        String puzzle = "....78.....3...59..9.2...1...4..6......134......7..68..2...9.7...8...3.....32....";
        String solved = "152978463783461592496253718274586931869134257531792684325849176948617325617325849";
        runTest(puzzle, solved, new String[]{"X-Cycles"}, new int[]{1}, false);
    }

    @Test
    void xCyclesTest5() {
        String puzzle = "....5......12.39...5...7...9...2...8.6.7.8.2.4...6...1...5...8...73.45......1....";
        String solved = "738159246641283975259647813973421658165738429482965731396572184817394562524816397";
        runTest(puzzle, solved, new String[]{"X-Cycles"}, false);
    }

}
