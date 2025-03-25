package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenQuads implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() >= 2 && list.size() <= 4);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j) {
                            continue;
                        }
                        for (int k : map.keySet()) {
                            if (i == k || j == k) {
                                continue;
                            }
                            for (int l : map.keySet()) {
                                if (l == i || l == j || l == k) {
                                    continue;
                                }
                                Set<Cell> quad = new HashSet<>();
                                quad.addAll(map.get(i));
                                quad.addAll(map.get(j));
                                quad.addAll(map.get(k));
                                quad.addAll(map.get(l));
                                if (quad.size() != 4) {
                                    continue;
                                }
                                for (Cell cell : quad) {
                                    List<Integer> removed = cell.removeAllBut(List.of(i, j, k, l));
                                    if (!removed.isEmpty()) {
                                        changed.add(cell);
                                        sb.append(String.format("Hidden quad (%s, %s, %s, %s) in %s. Removed %s from %s%n", i, j, k, l, quad.stream().map(Cell::getPosition).collect(Collectors.joining(", ")), removed, cell.getPosition()));
                                    }
                                }
                                if (!changed.isEmpty()) incrementCounter(counter);
                            }
                        }
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
