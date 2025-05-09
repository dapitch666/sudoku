package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenQuads extends SolvingTechnique {
    public HiddenQuads() {
        super("Hidden Quads", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(Predicates.inUnit(unitType, unitIndex)), list -> list.size() >= 2 && list.size() <= 4);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j) continue;
                        for (int k : map.keySet()) {
                            if (i == k || j == k) continue;
                            for (int l : map.keySet()) {
                                if (l == i || l == j || l == k) continue;
                                Set<Cell> quad = new HashSet<>();
                                quad.addAll(map.get(i));
                                quad.addAll(map.get(j));
                                quad.addAll(map.get(k));
                                quad.addAll(map.get(l));
                                if (quad.size() != 4) continue;
                                for (Cell cell : quad) {
                                    BitSet removed = cell.removeAllBut(List.of(i, j, k, l));
                                    if (removed.isEmpty()) continue;
                                    changed.add(cell);
                                    log("- Removed %s from %s%n", removed, cell);
                                }
                                if (changed.isEmpty()) continue;
                                log(0, "Hidden quad {%s, %s, %s, %s} in %s, on cells %s%n", i, j, k, l, unitType.toString(unitIndex), quad);
                                incrementCounter();
                                return changed;
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
