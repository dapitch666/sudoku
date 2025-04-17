package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.List;

public class BiValueUniversalGrave extends SolvingTechnique {
    public BiValueUniversalGrave() {
        super("BiValue Universal Grave", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        int unsolvedCells = grid.getUnsolvedCells().length;
        Cell[] cells = grid.getCellsWithNCandidates(3);
        if (cells.length != 1) return List.of();
        if (grid.getCellsWithNCandidates(2).length == unsolvedCells - 1) {
            Cell cell = cells[0];
            int[] candidates = cell.getCandidates().stream().mapToInt(Integer::intValue).toArray();
            for (int digit : candidates) {
                if (grid.getCellsInUnitWithCandidate(digit, UnitType.ROW, cell.getRow()).length > 2
                || grid.getCellsInUnitWithCandidate(digit, UnitType.COL, cell.getCol()).length > 2
                || grid.getCellsInUnitWithCandidate(digit, UnitType.BOX, cell.getBox()).length > 2) {
                    cell.removeAllBut(List.of(digit));
                    log("BUG found in %s. %d must be the solution%n", cell, digit);
                    incrementCounter();
                    return List.of(cell);
                }
            }
        }
        return List.of();
    }
}
