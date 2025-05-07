package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenTriples extends SolvingTechnique {
    public HiddenTriples() {
        super("Hidden Triples", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(Predicates.inUnit(unitType, unitIndex)), list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j) continue;
                        for (int k : map.keySet()) {
                            if (i == k || j == k) continue;
                            Set<Cell> triple = new HashSet<>();
                            triple.addAll(map.get(i));
                            triple.addAll(map.get(j));
                            triple.addAll(map.get(k));
                            if (triple.size() != 3) continue;

                            for (Cell cell : triple) {
                                BitSet removed = cell.removeAllBut(List.of(i, j, k));
                                if (removed.isEmpty()) continue;
                                changed.add(cell);
                                log("- Removed %s from %s%n", removed, cell);
                            }
                            if (changed.isEmpty()) continue;
                            log(0, "Hidden triple {%s, %s, %s} in %s, on cells %s%n", i, j, k, unitType.toString(unitIndex), triple);
                            incrementCounter();
                            return changed;
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
