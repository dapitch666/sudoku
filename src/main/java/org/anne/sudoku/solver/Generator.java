package org.anne.sudoku.solver;

import org.anne.sudoku.model.Grid;
import org.anne.sudoku.utils.PrintUtils;
import org.anne.sudoku.utils.Timer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Generator {

    private final BruteForce solver = new BruteForce();

    public Grid generate() {
        Grid solution = new Grid();
        if (!solver.solveRandom(solution)) {
            throw new IllegalStateException("Failed to generate a valid Sudoku grid");
        }
        Grid grid = new Grid(solution.getSolution());

        List<Integer> indexes = IntStream.range(0, 81)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(indexes);

        for (int index : indexes) {
            grid.clear(index);
            if (!solver.hasUniqueSolution(grid)) {
                grid.set(index, solution.get(index), true);
            }
        }

        solver.solve(grid);
        return grid;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        Generator generator = new Generator();
        Grid grid = generator.generate();
        System.out.println(PrintUtils.printBoth(grid.getPuzzle(), grid.getSolution()));
        System.out.println("Clues: " + grid.getClueCount());
        System.out.println(timer.duration());
    }
}
