package org.anne.sudoku.solver.techniques;

import org.junit.jupiter.api.Test;

import static org.anne.sudoku.solver.SolverTest.runTest;

class JellyFishTest {
    @Test
    void jellyFishTest1() {
        String puzzle = ".........8.3.24.1.9.1.76.8.6.7.8392......91...........7.8.1..3..........1.2.3.69.";
        String solved = ".........8.3.24.1.9.1376.8.6.7.8392......91...........7.8.1..3.........11.2.3.69.";
        // Need Alternating Infer. Chains
        // String solved = "274891356863524719951376482617483925385269147429157863798612534536948271142735698";
        runTest(puzzle, solved, new String[]{"Jelly-Fish"}, new int[]{2}, false);
    }

    @Test
    void jellyFishTest2() {
        String puzzle = "14.....9797.....16............453....6.17....73..2.............42..6..7161.....39";
        String solved = "148536297973842516256791384891453762562178943734629158387914625429365871615287439";
        runTest(puzzle, solved, new String[]{"Jelly-Fish"}, new int[]{2}, false);
    }
}