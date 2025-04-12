package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwordFish extends SolvingTechnique {
    public SwordFish() {
        super("Sword Fish", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
            for (int digit = 1; digit <= 9; digit++) {
                List<Cell[]> list = new ArrayList<>();
                for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                    Cell[] cells = grid.getCellsInUnitWithCandidate(digit, unitType, unitIndex);
                    if (cells.length == 2 || cells.length == 3) {
                        list.add(cells);
                    }
                }
                // Find aligned cells
                for (int i = 0; i < list.size(); i++) {
                    Cell[] first = list.get(i);
                    for (int j = i + 1; j < list.size(); j++) {
                        Cell[] second = list.get(j);
                        for (int k = j + 1; k < list.size(); k++) {
                            Cell[] third = list.get(k);
                            List<Cell> swordfish = new ArrayList<>();
                            swordfish.addAll(Arrays.stream(first).toList());
                            swordfish.addAll(Arrays.stream(second).toList());
                            swordfish.addAll(Arrays.stream(third).toList());
                            List<Integer> unitsIndex;
                            if (unitType == UnitType.ROW) {
                                unitsIndex = swordfish.stream().map(Cell::getCol).distinct().toList();
                            } else {
                                unitsIndex = swordfish.stream().map(Cell::getRow).distinct().toList();
                            }
                            if (unitsIndex.size() == 3) {
                                List<Cell> changed = new ArrayList<>();
                                for (int col : unitsIndex) {
                                    for (Cell cell : grid.getCellsInUnitWithCandidate(digit, unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, col))
                                        if (!swordfish.contains(cell) && cell.removeCandidate(digit)) {
                                            changed.add(cell);
                                            log("Swordfish %d in %s. Removed %d from %s%n", digit, swordfish.stream().map(Cell::toString).toList(), digit, cell);
                                        }
                                }
                                if (!changed.isEmpty()) {
                                    incrementCounter();
                                    return changed;
                                }
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
