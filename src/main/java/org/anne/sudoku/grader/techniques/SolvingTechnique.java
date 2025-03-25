package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.List;

public interface SolvingTechnique {
    List<Cell> apply(Grid grid, StringBuilder sb);

    default void log(String message) {
        // System.out.println(message);
    }

    default void incrementCounter(int[] counter) {
        counter[0]++;
    }

    default String getName() {
        return this.getClass().getSimpleName()
                .replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2")
                .replaceAll("(\\p{Lu})(\\p{Lu})", "$1-$2");
    }

    int getCounter();
}
