package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.List;

public class NakedSingles implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        Cell[] cells = grid.getUnsolvedCells();
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : cells) {
            if (cell.getCandidateCount() == 1) {
                cell.setValue(cell.getFirstCandidate());
                sb.append(String.format("Last candidate, %d, in %s changed to solution%n", cell.getValue(), cell.getPosition()));
                changed.add(cell);
                incrementCounter(counter);
            }
        }
        if (!changed.isEmpty()) {
            log(sb.toString());
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}