package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoxLineReduction extends SolvingTechnique {
    public BoxLineReduction() {
        super("Box-Line Reduction", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                int finalUnitIndex = unitIndex;
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(cell -> cell.getUnitIndex(unitType) == finalUnitIndex), list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    boolean sameSquare = map.get(i).stream().map(Cell::getBox).distinct().count() == 1;
                    if (unitType == UnitType.ROW && sameSquare) {
                        for (Cell cell : grid.getCells(cell -> cell.getBox() == map.get(i).getFirst().getBox())) {
                            if (!cell.isSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log("Box-line reduction in %s: ", UnitType.ROW.toString(unitIndex));
                                    log("removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    } else if (unitType == UnitType.COL && sameSquare) {
                        for (Cell cell : grid.getCells(cell -> cell.getBox() == map.get(i).getFirst().getBox())) {
                            if (!cell.isSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log("Box-line reduction in %s: ", UnitType.COL.toString(unitIndex));
                                    log("removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    }
                    if (!changed.isEmpty()) {
                        incrementCounter();
                    }
                }
            }
        }
        return changed;
    }
}
