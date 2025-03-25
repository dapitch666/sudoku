package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class PointingPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(cells, Helper.listSizeLessThanThree);
        for (int i : map.keySet()) {
            boolean sameRow = map.get(i).stream().map(Cell::getRow).distinct().count() == 1;
            boolean sameCol = map.get(i).stream().map(Cell::getColumn).distinct().count() == 1;
            boolean sameSquare = map.get(i).stream().map(Cell::getSquare).distinct().count() == 1;
            if (unitType == UnitType.SQUARE && sameRow) {
                sb.append(String.format("%s between %s and %s:%n", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.ROW.toString(map.get(i).getFirst().getRow())));
                for (Cell cell : grid.getCells(UnitType.ROW, map.get(i).getFirst().getRow())) {
                    if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                        if (cell.removeCandidate(i)) {
                            changed.add(cell);
                            sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                        }
                    }
                }
            } else if (unitType == UnitType.SQUARE && sameCol) {
                sb.append(String.format("%s between %s and %s:%n", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.COLUMN.toString(map.get(i).getFirst().getColumn())));
                for (Cell cell : grid.getCells(UnitType.COLUMN, map.get(i).getFirst().getColumn())) {
                    if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                        if (cell.removeCandidate(i)) {
                            changed.add(cell);
                            sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                        }
                    }
                }
            } else if ((unitType == UnitType.ROW || unitType == UnitType.COLUMN) && sameSquare) {
                sb.append(String.format("%s between %s and %s:%n", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.SQUARE.toString(map.get(i).getFirst().getSquare())));
                for (Cell cell : grid.getCells(UnitType.SQUARE, map.get(i).getFirst().getSquare())) {
                    if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                        if (cell.removeCandidate(i)) {
                            changed.add(cell);
                            sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter(counter);
            log(sb.toString());
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
