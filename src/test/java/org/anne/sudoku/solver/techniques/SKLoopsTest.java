package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class SKLoopsTest {
    @Test
    void testSKLoops1() {
        String puzzle = "1.......2.9.4...5...6...7...5.9.3.......7.......85..4.7.....6...3...9.8...2.....1";
        String solved = "1.......2.9.4...5...6...7...5.9.3.......74......85..4.7.....6...3...9.8...2.....1";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, false);
    }

    @Test
    void testSKLoops2() {
        String puzzle = "1...2...3.4.....5...6...7.....5.6...8...9...1...3.......7...6...5.....9.2...3...8";
        String solved = "1...2...3.4.....5...6...7.....5.6...8...9...1...3.......7...6...5.....9.2...3...8";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, false);
    }

    @Test
    void testSKLoops3() {
        String puzzle = "2.......4.8.5...7...1.2.3.....7...9.....6.....7...8.....3...1...9...7.5.4....1..2";
        String solved = "259673814386514279741829365815732496934165728672498531523986147198247653467351982";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, false);
    }

    @Test
    void testSKLoops4() {
        String puzzle = "12.3.....34....1....5......6.24..5......6..7......8..6..42..3......7...9.....9.8.";
        String solved = "12.3.....34....1....5......6.24..5......6..7......8..6..42..3......7...9.....9.8.";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{1}, false);
    }

    @Test
    void testSKLoops5() {
        String puzzle = "..1..23...4..5..6.7.......8...8.......5.3.2.......4...8.......7.6..9..4...23..1..";
        String solved = "651782394248953761739416528326879415485631279197524683814265937563197842972348156";
        runTest(puzzle, solved, new String[]{"SK Loops"}, new int[]{2}, false);
    }
}
