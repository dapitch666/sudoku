package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class XWings extends SolvingTechnique {
    public XWings() {
        super("X-Wings", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
            for (int digit = 1; digit <= 9; digit++) {
                List<Cell[]> list = new ArrayList<>();
                for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                    Cell[] cells = grid.getCells(Predicates.inUnit(unitType, unitIndex).and(Predicates.containsCandidate(digit)));
                    if (cells.length == 2) {
                        list.add(cells);
                    }
                }
                for (int j = 0; j < list.size(); j++) {
                    for (int k = j + 1; k < list.size(); k++) {
                        List<Cell> xWing = List.of(list.get(j)[0], list.get(j)[1], list.get(k)[0], list.get(k)[1]);
                        List<Integer> unitIndices = xWing.stream().map(c -> unitType == UnitType.ROW ? c.getCol() : c.getRow()).distinct().toList();
                        if (unitIndices.size() != 2) continue;
                        List<Cell> changed = new ArrayList<>();

                        for (Cell cell : grid.getCells(
                                Predicates.inUnit(unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, unitIndices.getFirst())
                                        .or(Predicates.inUnit(unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, unitIndices.getLast()))
                                        .and(Predicates.containsCandidate(digit))
                                        .and(c -> !xWing.contains(c)))) {
                            cell.removeCandidate(digit);
                            changed.add(cell);
                        }
                        if (changed.isEmpty()) continue;
                        log(0, "X-Wing in %s%n- Removed candidate %d from %s%n", xWing, digit, changed);
                        incrementCounter();
                        return changed;
                    }
                }
            }
        }
        return List.of();
    }
}
