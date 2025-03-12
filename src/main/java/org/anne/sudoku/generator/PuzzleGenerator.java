package org.anne.sudoku.generator;

import org.anne.sudoku.Utils;
import org.anne.sudoku.solver.PrintUtils;
import org.anne.sudoku.solver.Sudoku;
import org.anne.sudoku.solver.Timer;

import java.util.Random;

import static org.anne.sudoku.solver.MultipleSolutionsFinder.countSolutions;

// TODO: Add a limit to the number of iterations to avoid endless loops
// TODO: Work with a Sudoku object instead of an array or use an array everywhere instead of a String

public class PuzzleGenerator {
    final String solution;

    public PuzzleGenerator(String solution) {
        this.solution = solution;
    }

    public String generate() {
        int[] puzzle = Utils.stringToArray(solution);
        Random rand = new Random();

        // First try to remove 4 numbers diagonally
        for (int i = 0; i < 20; i++) {
            int pos = rand.nextInt(81);
            int[] opposites = getDiagonalOpposites(pos);

            if (tryRemoveNumbers(puzzle, opposites)) {
                continue;
            }

            // If removing 4 numbers leads to unsolvable puzzle, try removing 2 numbers diagonally
            int[] pair = {pos, getOpposite(pos)};
            if (tryRemoveNumbers(puzzle, pair)) {
                continue;
            }
            break;
        }

        // Then remove numbers by pairs until there are 30 numbers left
        while (countRemainingNumbers(puzzle) > 30) {
            int pos = rand.nextInt(81);
            if (puzzle[pos] == 0) continue;

            int[] pair = {pos, getOpposite(pos)};
            tryRemoveNumbers(puzzle, pair);
        }

        // Eventually remove numbers one by one until there are 20 numbers left
        for (int i = 0; i < 81 && countRemainingNumbers(puzzle) > 20; i++) {
            if (puzzle[i] == 0) continue;

            int temp = puzzle[i];
            puzzle[i] = 0;

            if (hasMultipleSolutions(puzzle)) {
                puzzle[i] = temp;
            }
        }

        return Utils.arrayToString(puzzle);
    }

    private static boolean hasMultipleSolutions(int[] puzzle) {
        return countSolutions(new Sudoku(Utils.arrayToString(puzzle))) > 1;
    }

    private static boolean tryRemoveNumbers(int[] grid, int[] positions) {
        int[] backup = new int[positions.length];
        for (int i = 0; i < positions.length; i++) {
            backup[i] = grid[positions[i]];
            grid[positions[i]] = 0;
        }

        if (hasMultipleSolutions(grid)) {
            for (int i = 0; i < positions.length; i++) {
                grid[positions[i]] = backup[i];
            }
            return false;
        }

        return true;
    }

    private static int[] getDiagonalOpposites(int index) {
        int row = index / 9;
        int col = index % 9;
        int oRow = 8 - row;
        int oCol = 8 - col;

        return new int[] {
                index,
                row * 9 + oCol,
                oRow * 9 + col,
                oRow * 9 + oCol
        };
    }

    private static int getOpposite(int pos) {
        int row = pos / 9;
        int col = pos % 9;
        return (8 - row) * 9 + (8 - col);
    }

    private static int countRemainingNumbers(int[] grid) {
        int count = 0;
        for (int n : grid) {
            if (n != 0) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        SolutionGenerator solutionGenerator = new SolutionGenerator();
        // String solution = solutionGenerator.generate();
        String solution = "241679385873425619659813742962748531714532968385196427527361894138954276496287153";
        String puzzle = new PuzzleGenerator(solution).generate();
        System.out.println(PrintUtils.printBoth(puzzle, solution));
        System.out.println(timer.duration());
    }
}
