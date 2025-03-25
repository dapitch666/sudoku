package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class PointingPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                List<Cell> cells = Arrays.stream(grid.getCells(unitType, unitIndex)).filter(Cell::isNotSolved).toList();
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(cells, list -> list.size() == 2 || list.size() == 3);
                for (int i : map.keySet()) {
                    boolean sameRow = map.get(i).stream().map(Cell::getRow).distinct().count() == 1;
                    boolean sameCol = map.get(i).stream().map(Cell::getColumn).distinct().count() == 1;
                    boolean sameSquare = map.get(i).stream().map(Cell::getSquare).distinct().count() == 1;
                    if (unitType == UnitType.SQUARE && sameRow) {
                        for (Cell cell : grid.getCells(UnitType.ROW, map.get(i).getFirst().getRow())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    sb.append(String.format("%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.ROW.toString(map.get(i).getFirst().getRow())));
                                    sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                                }
                            }
                        }
                    } else if (unitType == UnitType.SQUARE && sameCol) {
                        for (Cell cell : grid.getCells(UnitType.COLUMN, map.get(i).getFirst().getColumn())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    sb.append(String.format("%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.COLUMN.toString(map.get(i).getFirst().getColumn())));
                                    sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                                }
                            }
                        }
                    } else if ((unitType == UnitType.ROW || unitType == UnitType.COLUMN) && sameSquare) {
                        for (Cell cell : grid.getCells(UnitType.SQUARE, map.get(i).getFirst().getSquare())) {
                            if (cell.isNotSolved() && !map.get(i).contains(cell)) {
                                if (cell.removeCandidate(i)) {
                                    changed.add(cell);
                                    sb.append(String.format("%s between %s and %s: ", map.get(i).size() == 2 ? "PAIR " : "TRIPLE ", unitType.toString(unitIndex), UnitType.SQUARE.toString(map.get(i).getFirst().getSquare())));
                                    sb.append(String.format("removed %s from %s:%n", i, cell.getPosition()));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter(counter);
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
