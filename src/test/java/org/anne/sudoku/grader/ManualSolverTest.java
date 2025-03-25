package org.anne.sudoku.grader;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ManualSolverTest {

    @Test
    void kidsTest() {
        // This puzzle can be solved with Naked Singles only
        String puzzle = "3..967..1.4.3.2.8..2.....7..7.....9....873...5...1...3..47.51..9.5...2.78..621..4";
        String solved = "358967421741352689629184375173546892492873516586219743264795138915438267837621954";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(49, solver.getCounter("Naked Singles"));
    }

    @Test
    void hiddenSinglesTest() {
        // This puzzle's maximum technique is Hidden Singles
        String puzzle = ".....4.284.6.....51...3.6.....3.1....87...14....7.9.....2.1...39.....5.767.4.....";
        String solved = "735164928426978315198532674249381756387256149561749832852617493914823567673495281";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(21, solver.getCounter("Hidden Singles"));
    }

    @Test
    void moderateTest() {
        // This puzzle uses Naked Triples and Hidden Pairs
        String puzzle = "72..96..3...2.5....8...4.2........6.1.65.38.7.4........3.8...9....7.2...2..43..18";
        String solved = "725196483463285971981374526372948165196523847548617239634851792819762354257439618";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(1, solver.getCounter("Hidden Pairs"));
        assertEquals(1, solver.getCounter("Naked Triples"));
    }

    @Test
    void nakedTriplesTest() {
        // This puzzle uses Naked Triples and Hidden Pairs
        String puzzle = "...........19..5..56.31..9.1..6...28..4...7..27...4..3.4..68.35..2..59...........";
        String solved = "928547316431986572567312894195673428384251769276894153749168235612435987853729641";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(3, solver.getCounter("Naked Pairs"));
        assertEquals(4, solver.getCounter("Naked Triples"));
    }

    @Test
    void hiddenTriplesTest() {
        // This puzzle uses Naked Triples and Hidden Pairs
        String puzzle = "3........97..1....6..583...2.....9..5..621..3..8.....5...435..2....9..56........1";
        String solved = "381976524975214638642583179264358917597621483138749265816435792423197856759862341";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(3, solver.getCounter("Hidden Triples"));
        assertEquals(1, solver.getCounter("Naked Triples"));
    }

    @Test
    void xWingsTest() {
        // This puzzle uses X-Wings as the maximum technique
        String puzzle = "1.....5694.2.....8.5...9.4....64.8.1....1....2.8.35....4.5...1.9.....4.2621.....5";
        String solved = "187423569492756138356189247539647821764218953218935674843592716975361482621874395";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(1, solver.getCounter("XWings"));
    }

    @Test
    void chuteRemotePairsTest() {
        // This puzzle uses Chute Remote Pairs as the maximum technique
        String puzzle = "...9.5..........12.6.....5.39..5..6....3....4.4..6..85.3.....9.85..1.......2.7...";
        String solved = "128975643975436812463182759397854261586321974241769385732548196859613427614297538";

        ManualSolver solver = new ManualSolver(puzzle);
        // Capture output
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        assertEquals(2, solver.getCounter("Chute Remote Pairs"));
    }
}


