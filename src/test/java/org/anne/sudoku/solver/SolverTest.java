package org.anne.sudoku.solver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    @Test
    void solveSudoku() {
        String puzzle = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1"
                .replaceAll("[^0-9]", "0");

        int[] expectedSolution = {
                6, 9, 7,  5, 3, 8,  1, 2, 4,
                4, 2, 1,  7, 9, 6,  8, 3, 5,
                3, 5, 8,  1, 4, 2,  6, 7, 9,

                1, 4, 3,  8, 2, 9,  7, 5, 6,
                2, 6, 5,  4, 7, 3,  9, 1, 8,
                8, 7, 9,  6, 5, 1,  3, 4, 2,

                5, 8, 6,  2, 1, 7,  4, 9, 3,
                9, 1, 4,  3, 6, 5,  2, 8, 7,
                7, 3, 2,  9, 8, 4,  5, 6, 1
        };

        Solver.solveSudoku(puzzle);
        assertArrayEquals(expectedSolution, Solver.grid);
    }
}