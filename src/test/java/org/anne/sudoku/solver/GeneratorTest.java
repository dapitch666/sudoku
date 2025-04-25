package org.anne.sudoku.solver;

import org.anne.sudoku.model.Grid;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    @Test
    void generateTest() {
        Generator generator = new Generator();

        // Generate a Sudoku puzzle
        Grid grid1 = generator.generate();

        // Check if the generated puzzle is not null and solved
        assertNotNull(grid1);
        assertTrue(grid1.isSolved());

        // Check if the generated puzzle has a valid number of clues (between 17 and 40)
        long clueCount = grid1.getClueCount();
        assertTrue(clueCount >= 17 && clueCount <= 40);

        // Check if another generated puzzle is different
        Grid grid = generator.generate();
        assertNotEquals(grid1.getPuzzle(), grid.getPuzzle());
    }

    @Test
    void generateTestWithDifferentClueCounts() {
        // This test should not take more than 4 seconds to run
        Generator generator = new Generator();

        // Generate multiple Sudoku puzzles in parallel and check their clue counts
        Map<Integer, Long> count = IntStream.range(0, 1000)
                .parallel()
                .mapToObj(_ -> generator.generate().getClueCount())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//        for (int clue : count.keySet()) {
//            System.out.println(clue + " : " + count.get(clue));
//        }

        // Check if the average clue count is within a reasonable range
        double averageClueCount = count.keySet().stream().mapToInt(Integer::intValue).average().orElse(0);
        assertTrue(averageClueCount >= 21 && averageClueCount <= 27, "Average clue count is out of expected range");
    }
}