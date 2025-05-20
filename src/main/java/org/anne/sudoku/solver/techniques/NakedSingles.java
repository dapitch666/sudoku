package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;

import java.util.ArrayList;
import java.util.List;

public class NakedSingles extends SolvingTechnique {
    public NakedSingles() {
        super("Naked Singles", Grade.VERY_EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : grid.getCells(Predicates.cellsWithNCandidates(1, 1))) {
            grid.set(cell.index(), cell.getFirstCandidate(), false);
            log("Last candidate {%d} in %s changed to solution%n", cell.getValue(), cell);
            changed.add(cell);
            incrementCounter();
        }
        return changed;
    }
}