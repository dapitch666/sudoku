package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class PointingPairs extends SolvingTechnique {
    public PointingPairs() {
        super("Pointing Pairs", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(Predicates.inUnit(unitType, unitIndex)), list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    boolean sameRow = map.get(i).stream().map(Cell::getRow).distinct().count() == 1;
                    boolean sameCol = map.get(i).stream().map(Cell::getCol).distinct().count() == 1;
                    boolean sameSquare = map.get(i).stream().map(Cell::getBox).distinct().count() == 1;
                    if (unitType == UnitType.BOX && sameRow) {
                        for (Cell cell : grid.getCells(Predicates.inUnit(UnitType.ROW, map.get(i).getFirst().getRow()).and(Predicates.unsolvedCells))) {
                            if (!map.get(i).contains(cell) && cell.removeCandidate(i)) {
                                changed.add(cell);
                                log("%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.ROW.toString(map.get(i).getFirst().getRow()));
                                log("removed %s from %s:%n", i, cell);
                            }
                        }
                    } else if (unitType == UnitType.BOX && sameCol) {
                        for (Cell cell : grid.getCells(Predicates.inUnit(UnitType.COL, map.get(i).getFirst().getCol()).and(Predicates.unsolvedCells))) {
                            if (!map.get(i).contains(cell) && cell.removeCandidate(i)) {
                                changed.add(cell);
                                log("%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.COL.toString(map.get(i).getFirst().getCol()));
                                log("removed %s from %s:%n", i, cell);
                            }
                        }
                    } else if ((unitType == UnitType.ROW || unitType == UnitType.COL) && sameSquare) {
                        for (Cell cell : grid.getCells(Predicates.inUnit(UnitType.BOX, map.get(i).getFirst().getBox()).and(Predicates.unsolvedCells))) {
                            if (!map.get(i).contains(cell) && cell.removeCandidate(i)) {
                                changed.add(cell);
                                log("%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.BOX.toString(map.get(i).getFirst().getBox()));
                                log("removed %s from %s:%n", i, cell);
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter();
        }
        return changed;
    }
}
