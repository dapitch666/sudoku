package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class XWings implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
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
                                        log(sb, "X-Wing %d in %s. Removed %d from %s%n", i, xWing.stream().map(Cell::getPosition).toList(), i, cell.getPosition());
                                    }
                                }
                            }
                            if (!changed.isEmpty()) {
                                incrementCounter(counter);
                                return changed;
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
