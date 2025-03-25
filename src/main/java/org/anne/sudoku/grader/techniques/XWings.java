package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class XWings implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int index, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            Set<Cell> xWing = new HashSet<>();
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] cells = grid.getCellsInUnitWithCandidate(i, unitType, unitIndex);
                if (cells.length == 2) {
                    xWing.add(cells[0]);
                    xWing.add(cells[1]);
                }
            }
            List<Integer> unitsIndex;
            if (unitType == UnitType.ROW) {
                unitsIndex = xWing.stream().map(Cell::getColumn).distinct().toList();
            } else {
                unitsIndex = xWing.stream().map(Cell::getRow).distinct().toList();
            }
            if (xWing.size() == 4 && unitsIndex.size() == 2) {
                for (int unitIndex : unitsIndex) {
                    for (Cell cell : grid.getCells(unitType == UnitType.ROW ? UnitType.COLUMN : UnitType.ROW, unitIndex)) {
                        List<Integer> removed = new ArrayList<>();
                        if (!xWing.contains(cell) && cell.removeCandidate(i)) {
                            removed.add(i);
                        }
                        if (!removed.isEmpty()) {
                            changed.add(cell);
                            sb.append(String.format("X-Wing %d in %s. Removed %d from %s%n", i, xWing.stream().map(Cell::getPosition).toList(), i, cell.getPosition()));
                        }
                    }
                }
                if (!changed.isEmpty()) incrementCounter(counter);
            }
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
