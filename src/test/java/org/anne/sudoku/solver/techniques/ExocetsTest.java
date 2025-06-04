package org.anne.sudoku.solver.techniques;


import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class ExocetsTest {
    @Test
    void ExocetsTest1() {
        String puzzle = "..7.2...493....6..6..3............5.2...1...8..69..4....37..9...2..5...1.....8...";
        String solved = "587629134931845627642371589198234756274516398356987412813762945729453861465198273";
        runTest(puzzle, solved, new String[]{"Exocets"}, new int[]{1}, false);
    }

    @Test
    void ExocetsTestDouble1() {
        String puzzle = "......7....71.9...68..7..1...1.9.6.....3...2..4......3..8.6.1..5......4......2..5";
        String solved = "213546798457189236689273514321894657896357421745621983938465172562718349174932865";
        runTest(puzzle, solved, new String[]{"Exocets"}, new int[]{1}, false);
    }

    @Test
    void ExocetsTestDouble2() {
        String puzzle = ".9.....3.2..........3.1.9.7..6..........6.37....1.8..6..8.3...1..7.8...3...4...5.";
        String solved = "795846132214793685683512947876359214159264378432178596948635721527981463361427859";
        runTest(puzzle, solved, new String[]{"Exocets"}, new int[]{1}, false);
    }

}