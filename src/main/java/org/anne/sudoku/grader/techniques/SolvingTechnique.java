package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.List;

public abstract class SolvingTechnique {
    String name;
    int counter;

    public SolvingTechnique(String name) {
        this.name = name;
        this.counter = 0;
    }

    public abstract List<Cell> apply(Grid grid, StringBuilder sb);

    public String getName() {
        return this.name;
    }

    public int getCounter() {
        return counter;
    }

    public void printCounters() {
        System.out.println(this.name + ": " + this.counter);
    }

    void incrementCounter() {
        counter++;
    }

    void log(StringBuilder sb, String pattern, Object... args) {
        sb.append(String.format(pattern, args));
    }

    void log(StringBuilder sb, int i, String pattern, Object... args) {
        sb.insert(i, String.format(pattern, args));
    }
}
