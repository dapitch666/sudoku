package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class SKLoopsTest {
    @Disabled
    @Test
    void testSKLoops1() {
        String puzzle = "1.......2.9.4...5...6...7...5.9.3.......7.......85..4.7.....6...3...9.8...2.....1";
        String solved = "100000002090400050006000700050903000000074000000850040700000600030009080002000001";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, true);
    }

    @Disabled // Need Unit Forcing Chain
    @Test
    void testSKLoops2() {
        String puzzle = "1...2...3.4.....5...6...7.....5.6...8...9...1...3.......7...6...5.....9.2...3...8";
        String solved = "100020003040000050006000700000506000800090001000300000007000600050000090200030008";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, true);
    }

    @Disabled // Need Aligned Pair Exclusion, WXYZ Wing and Alternating Infer. Chains
    @Test
    void testSKLoops3() {
        String puzzle = "2.......4.8.5...7...1.2.3.....7...9.....6.....7...8.....3...1...9...7.5.4....1..2";
        String solved = "259673814386514279741829365815732496934165728672498531523986147198247653467351982";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, true);
    }

    @Disabled
    @Test
    void testSKLoops4() {
        String puzzle = "12.3.....34....1....5......6.24..5......6..7......8..6..42..3......7...9.....9.8.";
        String solved = "120300000340000100005000000602400500000060070000008006004200300000070009000009080";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, true);
    }

    @Disabled
    @Test
    void testSKLoops5() {
        String puzzle = "..1..23...4..5..6.7.......8...8.......5.3.2.......4...8.......7.6..9..4...23..1..";
        String solved = "651782394248953761739416528326879415485631279197524683814265937563197842972348156";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{2}, true);
    }
}
