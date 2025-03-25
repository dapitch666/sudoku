package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BoxLineReduction implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COLUMN)) {
                List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(cells, Helper.listSizeLessThanThree);
                for (int i : map.keySet()) {
                    boolean sameSquare = map.get(i).stream().map(Cell::getSquare).distinct().count() == 1;
                    if (unitType == UnitType.ROW && sameSquare) {
                        for (Cell cell : grid.getCells(UnitType.SQUARE, map.get(i).getFirst().getSquare())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    sb.append(String.format("Box-line reduction in %s: ", UnitType.ROW.toString(unitIndex)));
                                    sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                                }
                            }
                        }
                    } else if (unitType == UnitType.COLUMN && sameSquare) {
                        for (Cell cell : grid.getCells(UnitType.SQUARE, map.get(i).getFirst().getSquare())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    sb.append(String.format("Box-line reduction in %s: ", UnitType.COLUMN.toString(unitIndex)));
                                    sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                                }
                            }
                        }
                    }
                    if (!changed.isEmpty()) {
                        incrementCounter(counter);
                        log(sb.toString());
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
