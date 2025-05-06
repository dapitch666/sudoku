package org.anne.sudoku.solver;

import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.utils.PrintUtils;
import org.anne.sudoku.utils.Timer;

import java.util.Collections;
import java.util.List;

public class BruteForce {

    private int backtrackCount = 0;

    public boolean solve(Grid grid) {
        return solve(grid, Order.NATURAL);
    }

    public boolean solveRandom(Grid grid) {
        return solve(grid, Order.RANDOM);
    }

    public boolean solveReverse(Grid grid) {
        return solve(grid, Order.REVERSE);
    }

    private boolean solve(Grid grid, Order order) {
        Cell cell = grid.getBestCell();
        if (cell == null) return true;

        for (int digit : prepareCandidates(cell, order)) {
            grid.set(cell.index(), digit, false);
            if (solve(grid, order)) return true;
            // grid.backtrack(cell.index(), digit);
            grid.clear(cell.index());
        }
        backtrackCount++;
        return false;
    }

    private List<Integer> prepareCandidates(Cell cell, Order order) {
        List<Integer> candidates = cell.getCandidates();
        if (order == Order.RANDOM) Collections.shuffle(candidates);
        else if (order == Order.REVERSE) Collections.reverse(candidates);
        return candidates;
    }

    public boolean hasUniqueSolution(Grid grid) {
        Grid grid1 = new Grid(grid.getPuzzle());
        if (!solve(grid1)) return false;
        Grid grid2 = new Grid(grid.getPuzzle());
        solveReverse(grid2);
        return grid1.getSolution().equals(grid2.getSolution());
    }

    private enum Order {
        NATURAL, RANDOM, REVERSE
    }

    public static void main(String[] args) {
        BruteForce bruteForce = new BruteForce();
        String puzzle = "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1";
        Grid grid = new Grid(puzzle);
        Timer timer = new Timer();
        if (bruteForce.solve(grid)) {
            System.out.println(PrintUtils.printBoth(grid.getPuzzle(), grid.getSolution()));
        } else {
            System.out.println("The Sudoku has no solution.");
        }
        System.out.println("Backtrack count: " + bruteForce.backtrackCount);
        System.out.println(timer.duration());
    }
}