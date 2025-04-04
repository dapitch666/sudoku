package org.anne.sudoku.grader;

import org.junit.jupiter.api.Disabled;
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
        runTest(puzzle, solved, new String[]{"Rectangle Elimination"}, new int[]{6}, false);
    }

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

    @Test
    void xyzWingsTest1() {
        String puzzle = ".72...68....7.....5...16.......281..2..371..6..456.......13...4.....7....15...89.";
        String solved = "472953681961784325583216947657428139298371456134569278829135764346897512715642893";
        runTest(puzzle, solved, new String[]{"Xyz Wings"}, new int[]{1}, false);
    }

    @Test
    void xyzWingsTest2() {
        String puzzle = "...1...........98.7.5.6231.1.9..74.3.........8.72..1.5.9174.8.2.53...........1...";
        String solved = "964183527312574986785962314129857463536419278847236195691745832253698741478321659";
        runTest(puzzle, solved, new String[]{"Xyz Wings"}, new int[]{1}, false);
    }

    @Test
    void biValueUniversalGraveTest1() {
        String puzzle = "..1...7.6736.....55......82....78......52.......139...392...5..6.....137.5....4..";
        String solved = "821953746736842915549761382415678293963524871278139654392417568684295137157386429";
        runTest(puzzle, solved, new String[]{"Bi Value Universal Grave"}, new int[]{1}, false);
    }

    @Test
    void biValueUniversalGraveTest2() {
        String puzzle = "2..4..5.1..1.38.9..3....7.8.7...2..3.6..9...5.4......9..4....6.62.3..8..81..47...";
        String solved = "289476531751238496436915728975182643362794185148563279594821367627359814813647952";
        runTest(puzzle, solved, new String[]{"Bi Value Universal Grave"}, new int[]{1}, false);
    }

    @Disabled // Need Hidden Unique Rectangle and WXYZ Wing
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
        runTest(puzzle, solved, new String[]{"X-Cycles", "X-YChains"}, new int[]{3, 1}, false);
    }

    @Test
    void xCyclesTest3() {
        String puzzle = ".4...58..7...1.9....3..71..4..7......5.9.8.4......2..8..95..7......2...5..41...9.";
        String solved = "142695873765813924893247156428751639351968247976432518239584761617329485584176392";
        runTest(puzzle, solved, new String[]{"X-Cycles", "X-YChains"}, new int[]{1, 1}, false);
    }

    @Test
    void xCyclesTest4() {
        String puzzle = "....78.....3...59..9.2...1...4..6......134......7..68..2...9.7...8...3.....32....";
        String solved = "152978463783461592496253718274586931869134257531792684325849176948617325617325849";
        runTest(puzzle, solved, new String[]{"X-Cycles"}, new int[]{1}, false);
    }

    @Disabled // Sometimes, X-Cycles is used once and sometimes two...
    @Test
    void xCyclesTest5() {
        String puzzle = "....5......12.39...5...7...9...2...8.6.7.8.2.4...6...1...5...8...73.45......1....";
        String solved = "738159246641283975259647813973421658165738429482965731396572184817394562524816397";
        runTest(puzzle, solved, new String[]{"X-Cycles"}, new int[]{1}, true); // TODO: It seems non deterministic...
    }

    @Test
    void xyChainsTest1() {
        String puzzle = ".8.1.3.7............14.8.2.57...1.39...6.9...92.8...51.3.9.52............1.7.2.6.";
        String solved = "684123975392576148751498326578241639143659782926837451837965214265314897419782563";
        runTest(puzzle, solved, new String[]{"X-YChains"}, new int[]{2}, false);
    }

    @Test
    void xyChainsTest2() {
        String puzzle = "..2...376.1..3.5.........9.9..85...1...3.4...2...97..3.8.........3.4..6.147...2..";
        String solved = "892415376416739582375682194934856721761324958258197643689273415523941867147568239";
        runTest(puzzle, solved, new String[]{"X-YChains"}, new int[]{2}, false);
    }

    @Test
    void xyChainsTest3() {
        String puzzle = ".3..7.45..728...9....91..6....5.....3.......7.....6....8..21....5...924..29.6..3.";
        String solved = "931672458672854193845913762298537614364198527517246389486321975153789246729465831";
        runTest(puzzle, solved, new String[]{"X-YChains"}, new int[]{2}, false);
    }

    @Test
    void xyChainsTest4() {
        String puzzle = ".938..1...8.5.143..7..........2.5.8...5.6.2...6.1.9..........5..583.6.2...9..231.";
        String solved = "593847162286591437174623895417235689935468271862179543321984756758316924649752318";
        runTest(puzzle, solved, new String[]{"X-YChains"}, new int[]{1}, false);
    }

    @Disabled
    @Test
    void xyChainsTest5() {
        String puzzle = "..3..1...8.........51..9.6..8....29....7...8.2...4.5.36..9.......2.84...41..5.6..";
        String solved = "923861457846375921751429368187536294534792186269148573675913842392684715418257639";
        runTest(puzzle, solved, new String[]{"X-YChains"}, new int[]{9}, true);
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
