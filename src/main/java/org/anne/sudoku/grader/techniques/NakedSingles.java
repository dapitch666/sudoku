package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.ArrayList;
import java.util.List;

public class NakedSingles implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : grid.getUnsolvedCells()) {
            if (cell.getCandidateCount() == 1) {
                cell.setValue(cell.getFirstCandidate());
                log(sb, "Last candidate, %d, in %s changed to solution%n", cell.getValue(), cell);
                changed.add(cell);
                incrementCounter(counter);
            }
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}