package org.anne.sudoku.solver;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    @Test
    void solve() {
        Sudoku sudoku = new Sudoku("85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.");

        int[] expectedSolution = {
                8, 5, 9, 6, 1, 2, 4, 3, 7,
                7, 2, 3, 8, 5, 4, 1, 6, 9,
                1, 6, 4, 3, 7, 9, 5, 2, 8,
                9, 8, 6, 1, 4, 7, 3, 5, 2,
                3, 7, 5, 2, 6, 8, 9, 1, 4,
                2, 4, 1, 5, 9, 3, 7, 8, 6,
                4, 3, 2, 9, 8, 1, 6, 7, 5,
                6, 1, 7, 4, 2, 5, 8, 9, 3,
                5, 9, 8, 7, 3, 6, 2, 4, 1
        };

        Solver.solve(sudoku);
        assertArrayEquals(expectedSolution, sudoku.solution);
    }

    @Test
    void testMainWithValidInput() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {"85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4."};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Executed in"));
        assertTrue(output.contains("ms"));
        assertTrue(output.contains("Solution:"));

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    void testMainWithInValidInput() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {"855..24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4."};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input"));

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    void testMainWithNoSolution() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {"85.7.24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4."};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("No solution found!"));

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    void testMainWithAlreadySolved() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {"859612437723854169164379528986147352375268914241593786432981675617425893598736241"};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Puzzle is already solved!"));

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    void testMainWithShortInput() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {"123456789"};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Executed in"));
        assertTrue(output.contains("ms"));
        assertTrue(output.contains("Solution:"));

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    void testMainWithLongInput() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {"85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4.123456789"};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Executed in"));
        assertTrue(output.contains("ms"));
        assertTrue(output.contains("Solution:"));

        // Reset the standard output
        System.setOut(System.out);
    }

    @Test
    void testMainWithEmptyInput() {
        // Capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = {""};
        Solver.main(args);

        // Verify the output contains expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Solution:"));

        // Reset the standard output
        System.setOut(System.out);
    }
}