package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

public class ExtendedUniqueRectanglesTest {
    @Test
    void extendedUniqueRectanglesTestType1_1() {
        String puzzle = "..7.2.3.58.2.6.91..3.9...........6.....246.....9...........2.7..96.5.2.44.8.7.5..";
        String solved = "917824365842365917635917428184739652573246189269581743351492876796158234428673591";
        runTest(puzzle, solved, new String[]{"Extended Unique Rectangles"}, new int[]{1}, false);
    }

@Test
    void extendedUniqueRectanglesTestType1_2() {
        String puzzle = ".2.31.7..3...9....8.5...2.....23....5.......9...769.....9...1.7........3..1.53.2.";
        String solved = "624318795317592468895647231978235614562184379143769582439826157256971843781453926";
        runTest(puzzle, solved, new String[]{"Extended Unique Rectangles"}, new int[]{1}, false);
    }

    @Disabled("Need Alternating Infer. Chain")
    @Test
    void extendedUniqueRectanglesTestType2() {
        String puzzle = "1...7...3......68.9..2.8....5...43....28915....16...4....1.7..5.79......5...4...6";
        String solved = "185476923237915684964238751856724319342891567791653842623187495479562138518349276";
        runTest(puzzle, solved, new String[]{"Extended Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void extendedUniqueRectanglesTestType4() {
        String puzzle = "8.......2.2.8...4...5..79......4.8..64.5.9.31..2.1......69..2...9...5.6.5.......3";
        String solved = "869451372127893546435267918351746829648529731972318654716934285293185467584672193";
        runTest(puzzle, solved, new String[]{"Extended Unique Rectangles"}, new int[]{1}, false);
    }
}
