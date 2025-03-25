package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class HiddenPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            List<Cell> possibleCells = new ArrayList<>();
            for (Cell cell : cells) {
                if (cell.isCandidate(i)) {
                    possibleCells.add(cell);
                }
            }
            if (possibleCells.size() == 2) {
                map.put(i, possibleCells);
            }
        }
        for (int i = 1; i <= 9; i++) {
            if (map.containsKey(i)) {
                for (int j = i + 1; j <= 9; j++) {
                    if (map.containsKey(j) && map.get(i).equals(map.get(j))) {
                        for (Cell cell : map.get(i)) {
                            List<Integer> removed = cell.removeAllBut(List.of(i, j));
                            if (!removed.isEmpty()) {
                                changed.add(cell);
                                sb.append(String.format("Hidden pair (%s, %s) in %s and %s. Removed %s from %s%n", i, j, map.get(i).get(0).getPosition(), map.get(i).get(1).getPosition(), removed, cell.getPosition()));
                            }
                        }
                        if (!changed.isEmpty()) incrementCounter(counter);
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
