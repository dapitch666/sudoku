package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HiddenSingles implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(cells, list -> list.size() == 1);
        for (int i : map.keySet()) {
            Cell cell = map.get(i).getFirst();
            List<Integer> removed = cell.removeAllBut(List.of(i));
            // cell.setValue(cell.getFirstCandidate());
            if (!removed.isEmpty()) {
                // cell.setValue(cell.getFirstCandidate());
                changed.add(cell);
                incrementCounter(counter);
                sb.append(String.format("%d found once at %s in %s, %s candidates removed%n", i, cell.getPosition(), unitType.toString(unitIndex), removed.size()));
                //sb.append(String.format("Last candidate, %s, in %s changed to solution%n", cell.getValue(), cell.getPosition()));
            }
        }
        /*if (!changed.isEmpty()) {
            for (Cell cell : changed) {
                if (cell.getCandidateCount() == 1) {
                    cell.setValue(cell.getFirstCandidate());
                    sb.append(String.format("Last candidate, %d, in %s changed to solution%n", cell.getValue(), cell.getPosition()));
                }
            }
            log(sb.toString());
        }*/
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}