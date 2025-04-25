package org.anne.sudoku.solver;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolverTest {

    @Test
    void moderateTest() {
        String puzzle = "72..96..3...2.5....8...4.2........6.1.65.38.7.4........3.8...9....7.2...2..43..18";
        String solved = "725196483463285971981374526372948165196523847548617239634851792819762354257439618";
        runTest(puzzle, solved, new String[]{"Hidden Pairs", "Naked Triples"}, new int[]{1, 1}, true);
    }

    public static void runTest(String puzzle, String solved, String[] techniques, int[] counts, boolean debug) {
        Solver solver = new Solver(puzzle);
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

    public static void runTest(String puzzle, String solved, String[] techniques, boolean debug) {
        Solver solver = new Solver(puzzle);
        // Capture output
        if (!debug) System.setOut(new PrintStream(new ByteArrayOutputStream()));

        solver.solve();

        // Reset the standard output
        if (!debug) System.setOut(System.out);

        assertEquals(solved, solver.grid.currentState());
        for (String technique : techniques) {
            assertTrue(solver.getCounter(technique) > 0);
        }
    }
}
