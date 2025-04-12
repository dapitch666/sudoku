package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HiddenSingles extends SolvingTechnique {
    public HiddenSingles() {
        super("Hidden Singles");
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) { // For each row, column, square
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() == 1);
                for (int i : map.keySet()) {
                    Cell cell = map.get(i).getFirst();
                    List<Integer> removed = cell.removeAllBut(List.of(i));
                    if (!removed.isEmpty()) {
                        changed.add(cell);
                        incrementCounter();
                        log("%d found once at %s in %s, %s candidates removed%n", i, cell, unitType.toString(unitIndex), removed.size());
                    }
                }
            }
        }
        return changed;
    }
}