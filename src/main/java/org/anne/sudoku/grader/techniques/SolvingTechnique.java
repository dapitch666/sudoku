package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.List;

public interface SolvingTechnique {
    List<Cell> apply(Grid grid, StringBuilder sb);

    default void printCounters() {
        System.out.println(getName() + ": " + getCounter());
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

    default void log(StringBuilder sb, String pattern, Object... args) {
        sb.append(String.format(pattern, args));
    }

    default void log(StringBuilder sb, int i, String pattern, Object... args) {
        sb.insert(i, String.format(pattern, args));
    }
}
