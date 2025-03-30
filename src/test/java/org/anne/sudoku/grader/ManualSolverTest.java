package org.anne.sudoku.grader;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ManualSolverTest {

    @Test
    void kidsTest() {
        String puzzle = "3..967..1.4.3.2.8..2.....7..7.....9....873...5...1...3..47.51..9.5...2.78..621..4";
        String solved = "358967421741352689629184375173546892492873516586219743264795138915438267837621954";
        runTest(puzzle, solved, new String[]{"Naked Singles"}, new int[]{49}, false);
    }

    @Test
    void hiddenSinglesTest() {
        String puzzle = ".....4.284.6.....51...3.6.....3.1....87...14....7.9.....2.1...39.....5.767.4.....";
        String solved = "735164928426978315198532674249381756387256149561749832852617493914823567673495281";
        runTest(puzzle, solved, new String[]{"Hidden Singles"}, new int[]{21}, false);
    }

    @Test
    void moderateTest() {
        String puzzle = "72..96..3...2.5....8...4.2........6.1.65.38.7.4........3.8...9....7.2...2..43..18";
        String solved = "725196483463285971981374526372948165196523847548617239634851792819762354257439618";
        runTest(puzzle, solved, new String[]{"Hidden Pairs", "Naked Triples"}, new int[]{1, 1}, false);
    }

    @Test
    void nakedTriplesTest() {
        String puzzle = "...........19..5..56.31..9.1..6...28..4...7..27...4..3.4..68.35..2..59...........";
        String solved = "928547316431986572567312894195673428384251769276894153749168235612435987853729641";
        runTest(puzzle, solved, new String[]{"Naked Pairs", "Naked Triples"}, new int[]{3, 5}, false);
    }

    @Test
    void hiddenTriplesTest() {
        String puzzle = "3........97..1....6..583...2.....9..5..621..3..8.....5...435..2....9..56........1";
        String solved = "381976524975214638642583179264358917597621483138749265816435792423197856759862341";
        runTest(puzzle, solved, new String[]{"Hidden Triples", "Naked Triples"}, new int[]{1, 5}, false);
    }

    @Test
    void xWingsTest() {
        String puzzle = "1.....5694.2.....8.5...9.4....64.8.1....1....2.8.35....4.5...1.9.....4.2621.....5";
        String solved = "187423569492756138356189247539647821764218953218935674843592716975361482621874395";
        runTest(puzzle, solved, new String[]{"X-Wings"}, new int[]{1}, false);
    }

    @Test
    void chuteRemotePairsTest() {
        String puzzle = "...9.5..........12.6.....5.39..5..6....3....4.4..6..85.3.....9.85..1.......2.7...";
        String solved = "128975643975436812463182759397854261586321974241769385732548196859613427614297538";
        runTest(puzzle, solved, new String[]{"Chute Remote Pairs"}, new int[]{2}, false);
    }

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

    @Test
    void yWingsTest() {
        String puzzle = "9...4.......6...31.2.....9....7...2...29356...7...2....6.....7351...9.......8...9";
        String solved = "931247586754698231628153794195764328482935617376812945869521473513479862247386159";
        runTest(puzzle, solved, new String[]{"Y-Wings"}, new int[]{2}, false);
    }

    @Test
    void rectangleElimination() {
        String puzzle = ".3.6.9.2....28....1.......9......65372..6..91365......2.......7....16....1.5.7.4.";
        String solved = "537649128649281735182753469891472653724365891365198274256834917473916582918527346";
        runTest(puzzle, solved, new String[]{"Rectangle Elimination"}, new int[]{7}, true);
    }

    private void runTest(String puzzle, String solved, String[] techniques, int[] counts, boolean debug) {
        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        if (!debug) System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        if (!debug) System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        for (int i = 0; i < techniques.length; i++) {
            assertEquals(counts[i], solver.getCounter(techniques[i]));
        }
    }
}
