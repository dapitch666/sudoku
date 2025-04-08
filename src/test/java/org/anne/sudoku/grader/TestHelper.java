package org.anne.sudoku.grader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHelper {
    public static void runTest(String puzzle, String solved, String[] techniques, int[] counts, boolean debug) {
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

    public static void runTest(String puzzle, String solved, String[] techniques, boolean debug) {
        ManualSolver solver = new ManualSolver(puzzle);
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
