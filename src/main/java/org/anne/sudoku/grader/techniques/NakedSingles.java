package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.ArrayList;
import java.util.List;

public class NakedSingles extends SolvingTechnique {
    public NakedSingles() {
        super("Naked Singles");
    }

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : grid.getUnsolvedCells()) {
            if (cell.getCandidateCount() == 1) {
                cell.setValue(cell.getFirstCandidate());
                log(sb, "Last candidate, %d, in %s changed to solution%n", cell.getValue(), cell);
                changed.add(cell);
                incrementCounter();
            }
        }
        return changed;
    }
}