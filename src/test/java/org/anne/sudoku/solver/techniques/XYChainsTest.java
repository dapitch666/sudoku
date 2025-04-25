package org.anne.sudoku.solver.techniques;


import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class XYChainsTest {
    @Test
    void xyChainsTest1() {
        String puzzle = ".8.1.3.7............14.8.2.57...1.39...6.9...92.8...51.3.9.52............1.7.2.6.";
        String solved = "684123975392576148751498326578241639143659782926837451837965214265314897419782563";
        runTest(puzzle, solved, new String[]{"XY-Chains"}, false);
    }

    @Test
    void xyChainsTest2() {
        String puzzle = "..2...376.1..3.5.........9.9..85...1...3.4...2...97..3.8.........3.4..6.147...2..";
        String solved = "892415376416739582375682194934856721761324958258197643689273415523941867147568239";
        runTest(puzzle, solved, new String[]{"XY-Chains"}, false);
    }

    @Test
    void xyChainsTest3() {
        String puzzle = ".3..7.45..728...9....91..6....5.....3.......7.....6....8..21....5...924..29.6..3.";
        String solved = "931672458672854193845913762298537614364198527517246389486321975153789246729465831";
        runTest(puzzle, solved, new String[]{"XY-Chains"}, false);
    }

    @Test
    void xyChainsTest4() {
        String puzzle = ".938..1...8.5.143..7..........2.5.8...5.6.2...6.1.9..........5..583.6.2...9..231.";
        String solved = "593847162286591437174623895417235689935468271862179543321984756758316924649752318";
        runTest(puzzle, solved, new String[]{"XY-Chains"}, false);
    }

    @Test
    void xyChainsTest5() {
        String puzzle = "..3..1...8.........51..9.6..8....29....7...8.2...4.5.36..9.......2.84...41..5.6..";
        String solved = "923861457846375921751429368187536294534792186269148573675913842392684715418257639";
        runTest(puzzle, solved, new String[]{"XY-Chains"}, false);
    }
}