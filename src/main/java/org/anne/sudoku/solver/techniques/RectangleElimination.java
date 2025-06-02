package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RectangleElimination extends SolvingTechnique {
    public RectangleElimination() {
        super("Rectangle Elimination", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
            for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
                Map<Integer, List<Cell>> map = Helper.getPossibleCellsMap(grid.getCells(Predicates.inUnit(unitType, unitIndex)), list -> list.size() == 2);
                for (int candidate : map.keySet()) {
                    if (map.get(candidate).stream().map(Cell::getBox).distinct().count() == 1) continue;
                    for (Cell hinge : map.get(candidate)) {
                        // Find a weak link in opposite direction
                        Cell[] oppositeCells = grid.getCells(
                                Predicates.inUnit(unitType.opposite(), unitType == UnitType.ROW ? hinge.getCol() : hinge.getRow())
                                        .and(Predicates.containsCandidate(candidate)));
                        if (oppositeCells.length == 2) continue;
                        for (Cell wing2 : oppositeCells) {
                            if (wing2.getBox() == hinge.getBox()) continue;
                            Cell wing1 = map.get(candidate).stream().filter(cell -> cell != hinge).findFirst().orElseThrow();
                            int oppositeBox = Helper.findFourthBox(hinge.getBox(), wing1.getBox(), wing2.getBox());
                            Cell[] oppositeBoxCellsWithCandidate = grid.getCells(Predicates.inUnit(UnitType.BOX, oppositeBox)
                                    .and(Predicates.containsCandidate(candidate)));
                            if (oppositeBoxCellsWithCandidate.length != 0 && Arrays.stream(oppositeBoxCellsWithCandidate)
                                    .allMatch(cell -> cell.isPeer(wing1) || cell.isPeer(wing2))) {
                                wing2.removeCandidate(candidate);
                                log("%d found in %s (hinge), %s and %s%n- Removed candidate %d from %s%n", candidate, hinge, wing1, wing2, candidate, wing2);
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
