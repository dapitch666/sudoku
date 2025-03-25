package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenQuads implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            List<Cell> candidates = new ArrayList<>();
            for (Cell cell : cells) {
                if (cell.isCandidate(i)) {
                    candidates.add(cell);
                }
            }
            if (candidates.size() >= 2 && candidates.size() <= 4) {
                map.put(i, candidates);
            }
        }
        for (int i = 1; i <= 9; i++) {
            if (map.containsKey(i)) {
                for (int j = i + 1; j <= 9; j++) {
                    if (map.containsKey(j)) {
                        for (int k = j + 1; k <= 9; k++) {
                            if (map.containsKey(k)) {
                                for (int l = k + 1; l < 9; l++) {
                                    if (map.containsKey(l)) {
                                        Set<Cell> quad = new HashSet<>();
                                        quad.addAll(map.get(i));
                                        quad.addAll(map.get(j));
                                        quad.addAll(map.get(k));
                                        quad.addAll(map.get(l));
                                        if (quad.size() == 4) {
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
                }
            }
        }
        if (!changed.isEmpty()) {
            log(sb.toString());
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
