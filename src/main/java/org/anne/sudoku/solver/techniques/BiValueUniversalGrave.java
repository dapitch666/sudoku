package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.BitSet;
import java.util.List;

public class BiValueUniversalGrave extends SolvingTechnique {
    public BiValueUniversalGrave() {
        super("BiValue Universal Grave", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        Cell[] cells = grid.getCells(Predicates.cellsWithNCandidates(3));
        if (cells.length != 1 ||
                grid.getCells(Predicates.cellsWithNCandidates(2)).length != grid.getCells(Predicates.unsolvedCells).length - 1) {
            return List.of();
        }
        Cell cell = cells[0];
        for (int digit : cell.getCandidates()) {
            if (grid.getCells(Predicates.inUnit(UnitType.ROW, cell.getRow()).and(Predicates.hasCandidate(digit))).length > 2
                    || grid.getCells(Predicates.inUnit(UnitType.COL, cell.getCol()).and(Predicates.hasCandidate(digit))).length > 2
                    || grid.getCells(Predicates.inUnit(UnitType.BOX, cell.getBox()).and(Predicates.hasCandidate(digit))).length > 2) {
                BitSet removed = cell.removeAllBut(List.of(digit));
                log("BUG found in %s. %d must be the solution%n- Removed candidate(s) %s from %s%n", cell, digit, removed, cell);
                incrementCounter();
                return List.of(cell);
            }
        }
        return List.of();
    }
}
