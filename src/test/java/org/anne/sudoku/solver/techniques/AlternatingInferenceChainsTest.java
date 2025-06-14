package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class AlternatingInferenceChainsTest {

    @Test
    void alternatingInferenceChainsTest1() {
        String puzzle = ".....1....4.28.91...1..3.68..3...1.7.8.......6...473....8.....2.95.3.........4...";
        String solved = "869751234347286915521493768953862147784319526612547389438175692295638471176924853";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }

    @Test
    void alternatingInferenceChainsTest2() {
        String puzzle = "....4..3.2.7..96.....236.7.9.....2.7.........8.2.....4.5.972.....64..3.9.2..8....";
        String solved = "168745932237819645594236178943568217675124893812397564351972486786451329429683751";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }

    @Test
    void alternatingInferenceChainsTest3() {
        String puzzle = "543.....6......1.....7.2...7...9...4..16.8...6...3.5.1...8.3.....2......1.....328";
        String solved = "543189276267345189819762453735291864491658732628437591974823615382516947156974328";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }

    @Test
    void alternatingInferenceChainsTest4() {
        String puzzle = "..6.3..2..3...264.7.......126.5.18....8...2....32.8.653.......6.973...8..1..4.9..";
        String solved = "846139527931752648725684391269571834158463279473298165384925716597316482612847953";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{3}, false);
    }

    @Test
    void alternatingInferenceChainsTest5() {
        String puzzle = "....2......2..83....7.9...4..42.1..71.5.......8..3.....398..5.....9.5.4..7..1....";
        String solved = "943526718612748395857193264394251687125687439786439152439862571261975843578314926";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }

    @Test
    void alternatingInferenceChainsTest6() {
        String puzzle = "9.3..4...........4.8..1....1.....86....5..3..6...9...5..89.........7..43.54..2..6";
        String solved = "923654178571289634486317259195423867847561392632798415368945721219876543754132986";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }

    @Test
    void alternatingInferenceChainsTest7() {
        String puzzle = "....24.9....3.1.7.4....5..8..7....54..4...3..21....9..8..7....2.4.1.9....5.48....";
        String solved = "175824693628391475439675218967213854584967321213548967891736542342159786756482139";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{2}, false);
    }

    @Test
    void alternatingInferenceChainsTest8() {
        String puzzle = "......9..6..1.3..5..129.6..........8.28.4.36.7..........7.194..5..3.6..7..9......";
        String solved = "273658914694173825851294673416932758928745361735861249367519482542386197189427536";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{3}, false);
    }

    @Test
    void alternatingInferenceChainsTest9() {
        String puzzle = "4.5...3..3.21.7......8..14......6..817....4..6......15..7..9.....9..........3.5..";
        String solved = "415692387382147659796853142954216738173985426628374915537429861849561273261738594";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{2}, false);
    }

    @Test
    void alternatingInferenceChainsTest10() {
        String puzzle = "7.....5....84.....1...897...5.........4..2..8.....4.5.....2.8..43.76.....1.3....9";
        String solved = "796213584328457961145689732251836497674592318983174256567921843439768125812345679";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }

    @Test
    void alternatingInferenceChainsTest11() {
        String puzzle = ".1.......7.94...65...7.32..5..1.48.............89.6..3..62.9...19...84.6.......7.";
        String solved = "813562947729481365654793281567134892931827654248956713476219538195378426382645179";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{11}, false);
    }

    @Test
    void alternatingInferenceChainsALSTest1() {
        String puzzle = ".5.........7.4.........7364....36....841...7.7.3......3.1.8.9....9.....8...9..42.";
        String solved = "456319782837642195912857364125736849684195273793428516361284957249571638578963421";
        runTest(puzzle, solved, new String[]{"Alternating Inference Chains"}, new int[]{1}, false);
    }
}