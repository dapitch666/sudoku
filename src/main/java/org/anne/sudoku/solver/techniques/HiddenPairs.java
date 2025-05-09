package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

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
                int finalUnitIndex = unitIndex;
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(cell -> cell.getUnitIndex(unitType) == finalUnitIndex), list -> list.size() == 2);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j || !map.get(i).equals(map.get(j))) continue;
                        Map<Cell, BitSet> removedMap = new HashMap<>();
                        for (Cell cell : map.get(i)) {
                            BitSet removed = cell.removeAllBut(List.of(i, j));
                            if (removed.isEmpty()) continue;
                            removedMap.put(cell, removed);
                        }
                        if (removedMap.isEmpty()) continue;
                        incrementCounter();
                        log("Hidden pair {%d, %d} in %s, on cells %s%n", i, j, unitType.toString(unitIndex), map.get(i));
                        removedMap.keySet().forEach(cell -> log("- Removed candidate(s) %s from %s%n", removedMap.get(cell), cell));
                        changed.addAll(removedMap.keySet());
                    }
                }
            }
        }
        return changed;
    }
}
