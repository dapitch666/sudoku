package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

public class ThreeDMedusaTest {
    @Test
    void ThreeDMedusaTest1() {
        String puzzle = "3...5....25.3...1...46.75...9.2..8.5.7.....3.4.8..5.6...54.83...3...6.84....2...6";
        String solved = "386152497257349618914687523693271845571864239428935761165498372732516984849723156";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{3}, false);
    }

    @Test
    void ThreeDMedusaTest2() {
        String puzzle = "1...56....43.9....8....3..2.......1.95.421.37.2.......3..9....5....1.97....67...1";
        String solved = "192856743543297186876143592734568219958421637621739854317982465265314978489675321";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{2}, false);
    }

    @Test
    void ThreeDMedusaTest3() {
        String puzzle = "986.2.3....4956.............73..5..96...1...31..3..27.............1437....1.8.694";
        String solved = "986721345314956827527834961873265419692417583145398276458679132269143758731582694";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{1}, true);
    }

    @Test
    void ThreeDMedusaTest4() {
        String puzzle = "9.8.2..76......1...7.....2...54...913..7.2..546...58...4.....5...6......21..7.3.4";
        String solved = "958321476624957183173864529785436291391782645462195837847613952536249718219578364";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{6}, true);
    }

    @Test
    void ThreeDMedusaTestRule1() {
        String puzzle = ".938.45....56.....2.6.7.....2..6..4....2.8....7..4..9.....1.7.3.....26....25.718.";
        String solved = "793824561485631972216975438321769845964258317578143296859416723147382659632597184";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{1}, false);
    }

    @Test
    void ThreeDMedusaTestRule2() {
        String puzzle = "5.368214.21459783668.3..5..3.52..9.4....5...11.84.975........1.7.61..29..31925...";
        String solved = "573682149214597836689314572365271984497853621128469753952746318746138295831925467";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{1}, false);
    }

    @Disabled // Need Hidden Unique Rectangle, WXYZ Wing and Alternating Infer. Chains
    @Test
    void ThreeDMedusaTestRule3() {
        String puzzle = ".5..2....192.....4..46..........8..5..69418..9..7..........63..3.....621....8..9.";
        String solved = "653427189192835764874619253247368915536941872918752436725196348389574621461283597";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{1}, true);
    }

    @Test
    void ThreeDMedusaTestRule4() {
        String puzzle = "5874126932.6.378..1....82....2..1748.5.7249..7148..5....524.1.9..1.854..42.17.3.5";
        String solved = "587412693246937851139568274962351748853724916714896532675243189391685427428179365";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{1}, false);
    }


    @Test
    void ThreeDMedusaTestRule5() {
        String puzzle = ".8.276.49.........2..3.9..8..1....6...7...8...9....5..9..6.8..3.........52.9.4...";
        String solved = "385276149749185326216349758831527964657491832492863571974658213168732495523914687";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{8}, false);
    }

    @Test
    void ThreeDMedusaTestRule6() {
        String puzzle = "9...6.5....1....4.3..7....8....584...6.....8...2.4.3..1....5..9.2....8....7.3...2";
        String solved = "948362571271589643356714928739258416564193287812647395183425769625971834497836152";
        runTest(puzzle, solved, new String[]{"3D Medusa"}, new int[]{1}, true);
    }

}
