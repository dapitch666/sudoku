package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RectangleElimination extends SolvingTechnique {
    public RectangleElimination() {
        super("Rectangle Elimination");
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(unitType, unitIndex), list -> list.size() == 2);
                for (int candidate : map.keySet()) {
                    if (map.get(candidate).stream().map(Cell::getBox).distinct().count() == 1) continue;
                    for (Cell hinge : map.get(candidate)) {
                        // Find a weak link in opposite direction
                        Cell[] oppositeCells = grid.getCellsInUnitWithCandidate(candidate, unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, unitType == UnitType.ROW ? hinge.getCol() : hinge.getRow());
                        if (oppositeCells.length == 2) continue;
                        for (Cell wing2 : oppositeCells) {
                            if (wing2.getBox() == hinge.getBox()) continue;
                            Cell wing1 = map.get(candidate).stream().filter(cell -> cell != hinge).findFirst().orElseThrow();
                            int oppositeBox = Helper.findFourthBox(hinge.getBox(), wing1.getBox(), wing2.getBox());
                            Cell[] oppositeBoxCellsWithCandidate = grid.getCellsInUnitWithCandidate(candidate, UnitType.BOX, oppositeBox);
                            if (oppositeBoxCellsWithCandidate.length != 0 && Arrays.stream(oppositeBoxCellsWithCandidate)
                                    .allMatch(cell -> cell.isPeer(wing1) || cell.isPeer(wing2))) {
                                wing2.removeCandidate(candidate);
                                log("%s found in Hinge %s, wing1 %s and wing2 %s, we can remove %s as candidate in %s%n", candidate, hinge, wing1, wing2, candidate, wing2);
                                incrementCounter();
                                return List.of(wing2);
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
