package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

import static org.anne.sudoku.model.UnitType.*;

public class IntersectionRemoval extends SolvingTechnique {
    public IntersectionRemoval() {
        super("Intersection Removal", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();

        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> candidatesMap = Helper.getPossibleCellsMap(
                        grid.getCells(Predicates.inUnit(unitType, unitIndex)),
                        list -> list.size() == 2 || list.size() == 3
                );

                for (var entry : candidatesMap.entrySet()) {
                    int candidate = entry.getKey();
                    List<Cell> cells = entry.getValue();

                    UnitType targetUnitType;
                    if (unitType == BOX && cells.stream().map(Cell::getRow).distinct().count() == 1) {
                        targetUnitType = ROW;
                    } else if (unitType == BOX && cells.stream().map(Cell::getCol).distinct().count() == 1) {
                        targetUnitType = COL;
                    } else if (unitType != BOX && cells.stream().map(Cell::getBox).distinct().count() == 1) {
                        targetUnitType = BOX;
                    } else {
                        continue;
                    }
                    int targetUnitIndex = cells.getFirst().getUnitIndex(targetUnitType);

                    List<Cell> eliminatedCells = new ArrayList<>();
                    for (Cell cell : grid.getCells(Predicates.inUnit(targetUnitType, targetUnitIndex).and(Predicates.unsolvedCells).and(c -> !cells.contains(c)))) {
                        if (cell.removeCandidate(candidate)) {
                            eliminatedCells.add(cell);
                        }
                    }
                    if (eliminatedCells.isEmpty()) continue;
                    incrementCounter();
                    log("%s between %s and %s%n- Removed %s from %s%n",
                            cells.size() == 2 ? "PAIR" : "TRIPLE", unitType.toString(unitIndex),
                            targetUnitType.toString(targetUnitIndex), candidate, eliminatedCells);
                    changed.addAll(eliminatedCells);
                }
            }
        }
        return changed;
    }
}