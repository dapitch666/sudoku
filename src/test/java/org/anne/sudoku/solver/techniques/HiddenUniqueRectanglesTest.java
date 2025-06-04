package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class HiddenUniqueRectanglesTest {

    @Test
    void hiddenUniqueRectanglesTestType1() {
        String puzzle = "..27.1.3....5.3.....9...58..3.....9.4.8...7.3.1.....6..46...1.....4.9....9.1.86..";
        String solved = "652781934184593276379246581237614895468952713915837462846375129721469358593128647";
        runTest(puzzle, solved, new String[]{"Hidden Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void hiddenUniqueRectanglesTestType2() {
        String puzzle = "65..4..........2..3....6..8.6.8..3......2.....41..9.6.2..1....7..3...4......7..31";
        String solved = "658241793419387256327596148962815374735624819841739562294153687173968425586472931";
        runTest(puzzle, solved, new String[]{"Hidden Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void hiddenUniqueRectanglesTestType2b() {
        String puzzle = "8....1.6..21.4.......5.67...69...24...........72...13...36.9.......2.39..1.4....6";
        String solved = "857291463621743985394586712169358247438172659572964138283619574746825391915437826";
        runTest(puzzle, solved, new String[]{"Hidden Unique Rectangles", "Aligned Pair Exclusion"}, new int[]{1, 1}, false);
    }

    @Test
    void hiddenUniqueRectanglesTestDoubleElimination() {
        String puzzle = ".6.45..3....37...8...19....75.2.4.....17.5..484.9.1......6.72.....5298.392.8.35.7";
        String solved = "168452739295376418374198652759234186631785924842961375583647291417529863926813547";
        runTest(puzzle, solved, new String[]{"Hidden Unique Rectangles"}, new int[]{1}, false);
    }
}