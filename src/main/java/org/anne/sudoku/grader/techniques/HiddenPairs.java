package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class HiddenPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(cells, list -> list.size() == 2);
                for (int i : map.keySet()) {
                    for (int j : map.keySet()) {
                        if (i == j || !map.get(i).equals(map.get(j))) {
                            continue;
                        }
                        for (Cell cell : map.get(i)) {
                            List<Integer> removed = cell.removeAllBut(List.of(i, j));
                            if (!removed.isEmpty()) {
                                changed.add(cell);
                                sb.append(String.format("Hidden pair (%s, %s) in %s and %s. Removed %s from %s%n", i, j, map.get(i).get(0).getPosition(), map.get(i).get(1).getPosition(), removed, cell.getPosition()));
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) incrementCounter(counter);
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
