package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class FinnedSwordFishTest {
    @Test
    void finnedSwordFishTest1() {
        String puzzle = "6.54.8..2....7...1.1.....8.9.82.5..4.........1..7.38.5.8.....2.5...2....7..8.16.9";
        String solved = "6.5418..28...72..121.....8.9.8265..4.5.18.2..1..7.38.5.8.....2.5...2...87..8.16.9";
        // Need Alternating Infer. Chains
        // String solved = "635418972894672531217539486978265314356184297142793865481956723569327148723841659";
        runTest(puzzle, solved, new String[]{"Finned SwordFish"}, new int[]{1}, true);
    }

    @Test
    void finnedSwordFishTest2() {
        String puzzle = "2.........7.6.4.3...5.7.8...8.....7.4..8.5..6.1.....5...1.9.4...2.4.6.8.6.......3";
        String solved = "2.....6.7.7.6.4.3..65.7.84.582.6..744.78.5..6.16.4..58..1.9.46..2.4.6.8.6.......3";
        // Need Alternating Infer. Chains
        // String solved = "234589617879614235165273849582961374497835126316742958751398462923456781648127593";
        runTest(puzzle, solved, new String[]{"Finned SwordFish"}, new int[]{1}, true);
    }

    @Test
    void finnedSwordFishTest3() {
        String puzzle = "..63.1...19......6..4.9.1...596...4.....3.....6...492...7.5.6..3......15...4.83..";
        String solved = "576381294198245736234796158759612843421839567863574921947153682382967415615428379";
        runTest(puzzle, solved, new String[]{"Finned SwordFish"}, new int[]{1}, true);
    }

    @Disabled ("Requires Alternating Infer. Chains")
    @Test
    void finnedSwordFishTest4() {
        String puzzle = "..3.8.1...9...7......6....2.1...3..4..2...5..3..9...7.7....4......1...9...5.2.6..";
        String solved = "573482169296317458481695732617253984942768513358941276769534821824176395135829647";
        runTest(puzzle, solved, new String[]{"Finned SwordFish"}, new int[]{2}, true);
    }
}