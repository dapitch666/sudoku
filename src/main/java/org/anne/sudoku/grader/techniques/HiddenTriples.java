package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

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
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j) {
                            continue;
                        }
                        for (int k : map.keySet()) {
                            if (i == k || j == k) {
                                continue;
                            }
                            Set<Cell> triple = new HashSet<>();
                            triple.addAll(map.get(i));
                            triple.addAll(map.get(j));
                            triple.addAll(map.get(k));
                            if (triple.size() == 3) {
                                for (Cell cell : triple) {
                                    List<Integer> removed = cell.removeAllBut(List.of(i, j, k));
                                    if (!removed.isEmpty()) {
                                        changed.add(cell);
                                        log("Hidden triple (%s, %s, %s) in %s. Removed %s from %s%n", i, j, k, triple.stream().map(Cell::toString).collect(Collectors.joining(", ")), removed, cell);
                                    }
                                }
                                if (!changed.isEmpty()) {
                                    incrementCounter();
                                    return changed;
                                }
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
