package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class GroupedXCyclesTest {
    @Test
    void groupedXCyclesTest1() {
        String puzzle = "...7.....62.........4.523...57.3..28.9.....1.13..7..6...549.6.........32.........";
        String solved = "513786249628349157974152386457631928896524713132978564285493671769815432341267895";
        runTest(puzzle, solved, new String[]{"Grouped X-Cycles"}, new int[]{4}, false);
    }

    @Disabled("Need Alternating Infer. Chains")
    @Test
    void groupedXCyclesTest2() {
        String puzzle = "185.2637..6........97..81...1..52.9.....6.....3.179.4..416..95...........5629.7..";
        String solved = "185.2637..6........97..81...1..52.9.....6.....3.179.4..416..95...........5629.7..";
        // String solved = "185926374263741589497538126714852693529463817638179245341687952972315468856294731";
        runTest(puzzle, solved, new String[]{"Grouped X-Cycles"}, new int[]{2}, false);
    }

    @Test
    void groupedXCyclesTest3() {
        String puzzle = "1.....46.3496........1.5..........7356..9..4....2......5..7..3...7..39.19.1......";
        String solved = "175329468349687215826145397298514673563798142714236589452971836687453921931862754";
        runTest(puzzle, solved, new String[]{"Grouped X-Cycles"}, new int[]{1}, false);
    }

    @Test
    void groupedXCyclesTest4() {
        String puzzle = "..39.2....7...68..98.......3..27...5.........6...35..74......69..63...7....8.94..";
        String solved = "563982741172546893984713256349278615857691324621435987438127569296354178715869432";
        runTest(puzzle, solved, new String[]{"Grouped X-Cycles"}, new int[]{3}, false);
    }

    @Test
    void groupedXCyclesTest5() {
        String puzzle = ".2..5..7.3..........71.63.....3.16.........1...62.7.....19..5..87.....399......82";
        String solved = "62..53.7131.........71.63.....3.16.........1.1.62.7.....19..5..87.....3996.....82";
        // Need Alternating Infer. Chains
        // String solved = "624853971318794256597126348759381624482569713136247895241938567875612439963475182";
        runTest(puzzle, solved, new String[]{"Grouped X-Cycles"}, new int[]{6}, false);
    }
}