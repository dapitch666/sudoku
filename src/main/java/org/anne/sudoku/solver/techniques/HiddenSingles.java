package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class HiddenSingles extends SolvingTechnique {
    public HiddenSingles() {
        super("Hidden Singles", Grade.VERY_EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) { // For each row, column, square
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                for (int i = 1; i <= 9; i++) { // For each candidate
                    // Get all cells in the unit that can contain the candidate
                    Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex)
                            .and(Predicates.hasCandidate(i)));
                    if (cells.length == 1) { // Only one cell can contain the candidate
                        Cell cell = cells[0];
                        BitSet removed = cell.removeAllBut(List.of(i));
                        if (removed.isEmpty()) continue;
                        changed.add(cell);
                        incrementCounter();
                        log("%d found once in %s.%n- Removed %s from %s%n", i, unitType.toString(unitIndex), removed, cell);
                    }
                }
            }
        }
        return changed;
    }
}