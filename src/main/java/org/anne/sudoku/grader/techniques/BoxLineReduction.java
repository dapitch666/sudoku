package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoxLineReduction implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    boolean sameSquare = map.get(i).stream().map(Cell::getBox).distinct().count() == 1;
                    if (unitType == UnitType.ROW && sameSquare) {
                        for (Cell cell : grid.getCells(UnitType.BOX, map.get(i).getFirst().getBox())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log(sb, "Box-line reduction in %s: ", UnitType.ROW.toString(unitIndex));
                                    log(sb, "removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    } else if (unitType == UnitType.COL && sameSquare) {
                        for (Cell cell : grid.getCells(UnitType.BOX, map.get(i).getFirst().getBox())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log(sb, "Box-line reduction in %s: ", UnitType.COL.toString(unitIndex));
                                    log(sb, "removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    }
                    if (!changed.isEmpty()) {
                        incrementCounter(counter);
                    }
                }
            }
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
