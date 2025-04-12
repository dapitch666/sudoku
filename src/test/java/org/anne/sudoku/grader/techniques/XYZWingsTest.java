package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.ManualSolverTest.runTest;

class XYZWingsTest {
    @Test
    void xyzWingsTest1() {
        String puzzle = ".72...68....7.....5...16.......281..2..371..6..456.......13...4.....7....15...89.";
        String solved = "472953681961784325583216947657428139298371456134569278829135764346897512715642893";
        runTest(puzzle, solved, new String[]{"XYZ-Wings"}, new int[]{1}, false);
    }

    @Test
    void xyzWingsTest2() {
        String puzzle = "...1...........98.7.5.6231.1.9..74.3.........8.72..1.5.9174.8.2.53...........1...";
        String solved = "964183527312574986785962314129857463536419278847236195691745832253698741478321659";
        runTest(puzzle, solved, new String[]{"XYZ-Wings"}, new int[]{1}, false);
    }
}