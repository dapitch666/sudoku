package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class HiddenPairs extends SolvingTechnique {
    public HiddenPairs() {
        super("Hidden Pairs", Grade.EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() == 2);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j || !map.get(i).equals(map.get(j))) {
                            continue;
                        }
                        for (Cell cell : map.get(i)) {
                            List<Integer> removed = cell.removeAllBut(List.of(i, j));
                            if (!removed.isEmpty()) {
                                changed.add(cell);
                                log("Hidden pair (%s, %s) in %s and %s. Removed %s from %s%n", i, j, map.get(i).get(0), map.get(i).get(1), removed, cell);
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) incrementCounter();
        return changed;
    }
}
