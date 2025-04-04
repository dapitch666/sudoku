package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class PointingPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    boolean sameRow = map.get(i).stream().map(Cell::getRow).distinct().count() == 1;
                    boolean sameCol = map.get(i).stream().map(Cell::getCol).distinct().count() == 1;
                    boolean sameSquare = map.get(i).stream().map(Cell::getBox).distinct().count() == 1;
                    if (unitType == UnitType.BOX && sameRow) {
                        for (Cell cell : grid.getCells(UnitType.ROW, map.get(i).getFirst().getRow())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log(sb, "%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.ROW.toString(map.get(i).getFirst().getRow()));
                                    log(sb, "removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    } else if (unitType == UnitType.BOX && sameCol) {
                        for (Cell cell : grid.getCells(UnitType.COL, map.get(i).getFirst().getCol())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log(sb, "%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.COL.toString(map.get(i).getFirst().getCol()));
                                    log(sb, "removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    } else if ((unitType == UnitType.ROW || unitType == UnitType.COL) && sameSquare) {
                        for (Cell cell : grid.getCells(UnitType.BOX, map.get(i).getFirst().getBox())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    log(sb, "%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.BOX.toString(map.get(i).getFirst().getBox()));
                                    log(sb, "removed %s from %s:%n", i, cell);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter(counter);
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
