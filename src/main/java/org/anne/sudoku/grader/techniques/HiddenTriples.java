package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;
import java.util.stream.Collectors;

public class HiddenTriples implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(cells, Helper.listSizeLessThanThree);
        for (int i = 1; i <= 9; i++) {
            if (map.containsKey(i)) {
                for (int j = i + 1; j <= 9; j++) {
                    if (map.containsKey(j)) {
                        for (int k = j + 1; k <= 9; k++) {
                            if (map.containsKey(k)) {
                                Set<Cell> triple = new HashSet<>();
                                triple.addAll(map.get(i));
                                triple.addAll(map.get(j));
                                triple.addAll(map.get(k));
                                if (triple.size() == 3) {
                                    for (Cell cell : triple) {
                                        List<Integer> removed = cell.removeAllBut(List.of(i, j, k));
                                        if (!removed.isEmpty()) {
                                            changed.add(cell);
                                            sb.append(String.format("Hidden triple (%s, %s, %s) in %s. Removed %s from %s%n", i, j, k, triple.stream().map(Cell::getPosition).collect(Collectors.joining(", ")), removed, cell.getPosition()));
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
