package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class WXYZWingsTest {
    @Disabled("Need Aligned Pair Exclusion and Alternating Infer. Chains")
    @Test
    void testWXYZWingsType1_and2_1() {
        String puzzle = ".....47..5..37..6.23......47...3.84....4.1....84.6...33......59.7..93..2..62.....";
        String solved = "169824735548379261237156984751932846623481597984567123312748659875693412496215378";
        // String solved = "169824735548379261237156984751932846623481597984567123312748659875693412496215378";
        runTest(puzzle, solved, new String[]{"WXYZ-Wings"}, new int[]{3}, false);
    }

    @Test
    void testWXYZWingsType1_1() {
        String puzzle = "8.2..37.9...1.4...5.......4..8...4.5.2.3.7.6.1.7...2..3.......1...9.1...2.98..3.7";
        String solved = "842563719693174582571289634938612475425397168167458293386725941754931826219846357";
        runTest(puzzle, solved, new String[]{"WXYZ-Wings"}, new int[]{1}, false);
    }

    @Test
    void testWXYZWingsType1_and2_2() {
        String puzzle = "....2....5..4....8.31..6.9..4.8.9.37.........98.2.3.5..6.7..48.2....4..6.........";
        String solved = "896327514572491368431586792145869237623175849987243651369712485258934176714658923";
        runTest(puzzle, solved, new String[]{"WXYZ-Wings"}, new int[]{2}, false);
    }

    @Disabled ("Need Aligned Pair Exclusion, Grouped X-Cycle and Alternating Infer. Chains")
    @Test
    void testWXYZWingsType2_1() {
        String puzzle = ".1......36.8...2.7..39..5.....6.4.1..3..5..2..4.3.2.....1..76..3.5...1.92......3.";
        String solved = "914275863658143297723968541572694318836751924149382756491537682385426179267819435";
        runTest(puzzle, solved, new String[]{"WXYZ-Wings"}, new int[]{2}, false);
    }


}