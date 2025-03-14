package org.anne.sudoku.generator;

import org.anne.sudoku.Utils;
import org.anne.sudoku.solver.MultipleSolutionsFinder;
import org.anne.sudoku.solver.PrintUtils;
import org.anne.sudoku.solver.Solver;
import org.anne.sudoku.solver.Sudoku;
import org.anne.sudoku.solver.Timer;

import java.util.*;

import static org.anne.sudoku.Constants.DIGITS;
import static org.anne.sudoku.Constants.N;

public class SolutionGenerator {

    public String generate() {
        Sudoku sudoku = new Sudoku("");
        while (MultipleSolutionsFinder.countSolutions(sudoku) != 1) {
            fillDiagonal(sudoku);
            fillFirstSquare(sudoku);
            fillLastSquare(sudoku);
            Solver.solve(sudoku);
        }
        return Utils.arrayToString(sudoku.grid);
    }

    private void fillDiagonal(Sudoku sudoku) {
        List<Integer> diagonal = new ArrayList<>(DIGITS);
        Collections.shuffle(diagonal);
        for (int i = 0; i < N; i++) {
            sudoku.set(i * 10, diagonal.get(i));
        }
    }

    private void fillFirstSquare(Sudoku sudoku) {
        List<Integer> firstSquare = new ArrayList<>(DIGITS);
        for (int i = 0; i < 3; i++) {
            firstSquare.remove((Integer) sudoku.get(i * 10));
        }
        Collections.shuffle(firstSquare);
        Deque<Integer> stack = new ArrayDeque<>(firstSquare);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int index = r * N + c;
                if (index % 10 != 0) {
                    sudoku.set(index, stack.pop());
                }
            }
        }
    }

    private void fillLastSquare(Sudoku sudoku) {
        List<Integer> lastSquare = new ArrayList<>(DIGITS);
        for (int i = 6; i < 9; i++) {
            lastSquare.remove((Integer) sudoku.get(i * 10));
        }
        Collections.shuffle(lastSquare);
        Deque<Integer> stack = new ArrayDeque<>(lastSquare);
        for (int r = 6; r < 9; r++) {
            for (int c = 6; c < 9; c++) {
                int index = r * N + c;
                if (index % 10 != 0) {
                    sudoku.set(index, stack.pop());
                }
            }
        }
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        SolutionGenerator solutionGenerator = new SolutionGenerator();
        String puzzle = solutionGenerator.generate();
        System.out.println(PrintUtils.printOne(puzzle));
        System.out.println(timer.duration());
    }
}
