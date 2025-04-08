package org.anne.sudoku.grader.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.grader.TestHelper.runTest;

public class UniqueRectanglesTest {
    @Disabled // need Avoidable Rectangle
    @Test
    void UniqueRectanglesTestType1() {
        String puzzle = ".16.98.2..2....784...........7..95.....3.7.....51..4...........681....5..5.67.81.";
        String solved = "716498325923561784548723691167249538894357162235186479372815946681934257459672813";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{2}, true);
    }

    @Test
    void UniqueRectanglesTestType2() {
        String puzzle = ".2........6....7948.9.6.2..7....3...9..1.2..3...5....8..4.2.5.7682....3........1.";
        String solved = "425971386163258794879364251741683925958142673236597148314829567682715439597436812";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void UniqueRectanglesTestType2b() {
        String puzzle = "......6..3....641....48.7......5.1...4.8.2.5...2.7......6729....946....7..1......";
        String solved = "419537628378296415265481793683954172147862359952173864836729541594618237721345986";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Disabled // Type 2c not implemented yet + Need Aligned Pair Exclusion and Alternating Infer. Chains
    @Test
    void UniqueRectanglesTestType2c() {
        String puzzle = "1...2..5....73.2..3..4....8..56.8.....2...4.....1..7..8....6.....1..7....3..1...2";
        String solved = "146829357598731264327465198415678923782953416963142785874296531251387649639514872";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, true);
    }

    @Test
    void UniqueRectanglesTestType3() {
        String puzzle = ".7........6..2..4.8.1...3....42.37..2...4...6..38.64....2...8.7.9..3..1........6.";
        String solved = "475381629369527148821469375654293781287145936913876452132654897596738214748912563";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, true);
    }
}
