package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class SolvingTechnique {
    private final String name;
    private final StringBuilder sb;
    private final Grade difficulty;
    private int counter;

    public SolvingTechnique(String name, Grade difficulty) {
        this.name = name;
        this.counter = 0;
        this.sb = new StringBuilder();
        this.difficulty = difficulty;
    }

    public abstract List<Cell> apply(Grid grid);

    public String getName() {
        return this.name;
    }

    public Grade getDifficulty() {
        return difficulty;
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

    void log(String pattern, Object... args) {
        sb.append(String.format(pattern, args));
    }

    void log(int i, String pattern, Object... args) {
        sb.insert(i, String.format(pattern, args));
    }

    protected List<Cell> removeCandidateFromCellsAndLog(List<Cell> cells, int digit) {
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : cells) {
            if (cell.removeCandidate(digit)) changed.add(cell);
        }
        if (changed.isEmpty()) return List.of();
        log("- Removed {%d} from %s%n", digit, changed);
        incrementCounter();
        return cells;
    }

    protected List<Cell> removeCandidatesFromCellAndLog(Cell cell, BitSet candidates) {
        BitSet removed = cell.removeCandidates(candidates);
        if (removed.isEmpty()) return List.of();
        log("- Removed %s from %s%n", removed, cell);
        incrementCounter();
        return List.of(cell);
    }

    public String getLog() {
        String message = sb.toString();
        sb.setLength(0);
        return message;
    }
}
