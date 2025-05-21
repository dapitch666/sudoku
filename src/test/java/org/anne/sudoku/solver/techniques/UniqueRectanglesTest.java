package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

public class UniqueRectanglesTest {
    @Test
    void uniqueRectanglesTestType1() {
        String puzzle = ".16.98.2..2....784...........7..95.....3.7.....51..4...........681....5..5.67.81.";
        String solved = ".16.98325.2.5617845.8.23..11672495388..3571....518647....815.4.681934257.5.67281.";
        // Need Avoidable Rectangle
        // String solved = "716498325923561784548723691167249538894357162235186479372815946681934257459672813";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{2}, false);
    }

    @Test
    void uniqueRectanglesTestType1_2() {
        String puzzle = "...8.6...2...1..74..97...1...6...2.13.....6...2........3...5.....2....8.81...2953";
        String solved = "173846529268519374549723816796458231381297645425361798937185462652934187814672953";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }


    @Test
    void uniqueRectanglesTestType2() {
        String puzzle = ".2........6....7948.9.6.2..7....3...9..1.2..3...5....8..4.2.5.7682....3........1.";
        String solved = "425971386163258794879364251741683925958142673236597148314829567682715439597436812";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType2_1() {
        String puzzle = "..........7418..6..2...4.3..8.......6....9...2..356......2.31.......164..65...9..";
        String solved = "951632487374185269826974531589427316643819752217356894498263175732591648165748923";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType2_2() {
        String puzzle = "...8.6...2...1..74..97...1...6...2.13.....6...2........3...5.....2....8.81...2953";
        String solved = "173846529268519374549723816796458231381297645425361798937185462652934187814672953";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType2b() {
        String puzzle = "......6..3....641....48.7......5.1...4.8.2.5...2.7......6729....946....7..1......";
        String solved = "419537628378296415265481793683954172147862359952173864836729541594618237721345986";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Disabled ("Need Alternating Infer. Chain")
    @Test
    void uniqueRectanglesTestType2c() {
        String puzzle = "..9....5..3.8.7.......9.8...9.6..13.7..9.1..4.15..4.9...2.8.......1.3.7..6....4..";
        String solved = "879462351536817942421395867294678135783951624615234798342786519958143276167529483";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType3() {
        String puzzle = ".7........6..2..4.8.1...3....42.37..2...4...6..38.64....2...8.7.9..3..1........6.";
        String solved = "475381629369527148821469375654293781287145936913876452132654897596738214748912563";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType3_1() {
        String puzzle = "65.7.8....2..6.......3...428....2..11..49.....9..3.....8...9.5....5...8.......3..";
        String solved = "654728139321964578978315642837652491165497823492831765783149256216573984549286317";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType3_2() {
        String puzzle = ".8.72..13.........1..3..........6.31.3.5.4.6..2.91.7..2..1..9.8..5..8..4.........";
        String solved = "489725613763841592152369847597286431831574269624913785246137958375698124918452376";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType3_3() {
        String puzzle = "1......4..6..1.....7.9...56.....9.....9..5.3.51..7..9.......2..8.6...3.72.4......";
        String solved = "128657943965413872473982156387149625649825731512376498791538264856294317234761589";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType3_4() {
        String puzzle = "....24...2....531.8...7..2..6..............95..4.9...2..1..9......6.......75.1.64";
        String solved = "173924856249865317856173429968257143732418695514396782621749538485632971397581264";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{2}, false);
    }

    @Test
    void uniqueRectanglesTestType3b2() {
        String puzzle = "91.24........8.6.......67..5....49.2.3.....7.7.48....6..53.......3.2........79.43";
        String solved = "916247835257983614348156729561734982839562471724891356495318267673425198182679543";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }

    @Test
    void uniqueRectanglesTestType3and4() {
        String puzzle = "3..2..64...5.4........5.9....2....9..5......6.9.....514..863....6...78.....4..3.2";
        String solved = "319278645685349127247156983732615498154982736896734251421863579963527814578491362";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{3}, false);
    }

    @Test
    void uniqueRectanglesTestType5() {
        String puzzle = "....3.....7342..6......64.3387961245124.8.6399653428177.1658....5..93.8......4...";
        String solved = "648135972573429168219876453387961245124587639965342817791658324456293781832714596";
        runTest(puzzle, solved, new String[]{"Unique Rectangles"}, new int[]{1}, false);
    }
}
