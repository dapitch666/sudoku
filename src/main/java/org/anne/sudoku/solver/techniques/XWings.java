package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class XWings extends SolvingTechnique {
    public XWings() {
        super("X-Wings", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
            for (int i = 1; i <= 9; i++) {
                List<Cell[]> list = new ArrayList<>();
                for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                    Cell[] cells = grid.getCellsInUnitWithCandidate(i, unitType, unitIndex);
                    if (cells.length == 2) {
                        list.add(cells);
                    }
                }
                for (int j = 0; j < list.size(); j++) {
                    for (int k = j + 1; k < list.size(); k++) {
                        List<Cell> xWing = List.of(list.get(j)[0], list.get(j)[1], list.get(k)[0], list.get(k)[1]);
                        List<Integer> unitsIndex;
                        if (unitType == UnitType.ROW) {
                            unitsIndex = xWing.stream().map(Cell::getCol).distinct().toList();
                        } else {
                            unitsIndex = xWing.stream().map(Cell::getRow).distinct().toList();
                        }
                        if (unitsIndex.size() == 2) {
                            List<Cell> changed = new ArrayList<>();
                            for (int unitIndex : unitsIndex) {
                                for (Cell cell : grid.getCells(unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, unitIndex)) {
                                    List<Integer> removed = new ArrayList<>();
                                    if (!xWing.contains(cell) && cell.removeCandidate(i)) {
                                        removed.add(i);
                                    }
                                    if (!removed.isEmpty()) {
                                        changed.add(cell);
                                        log("X-Wing %d in %s. Removed %d from %s%n", i, xWing.stream().map(Cell::toString).toList(), i, cell);
                                    }
                                }
                            }
                            if (!changed.isEmpty()) {
                                incrementCounter();
                                return changed;
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
