package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JellyFish extends SolvingTechnique {
    public JellyFish() {
        super("Jelly-Fish", Grade.VERY_HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (UnitType unitType : List.of(UnitType.ROW, UnitType.COL)) {
            for (int digit = 1; digit <= 9; digit++) {
                List<Cell[]> list = new ArrayList<>();
                for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                    Cell[] cells = grid.getCellsInUnitWithCandidate(digit, unitType, unitIndex);
                    if (cells.length >= 2 && cells.length <= 4) {
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
                            for (int l = k + 1; l < list.size(); l++) {
                                Cell[] fourth = list.get(l);
                                List<Cell> jellyFish = new ArrayList<>();
                                jellyFish.addAll(Arrays.stream(first).toList());
                                jellyFish.addAll(Arrays.stream(second).toList());
                                jellyFish.addAll(Arrays.stream(third).toList());
                                jellyFish.addAll(Arrays.stream(fourth).toList());
                                List<Integer> unitsIndex;
                                if (unitType == UnitType.ROW) {
                                    unitsIndex = jellyFish.stream().map(Cell::getCol).distinct().toList();
                                } else {
                                    unitsIndex = jellyFish.stream().map(Cell::getRow).distinct().toList();
                                }
                                if (unitsIndex.size() == 4) {
                                    List<Cell> changed = new ArrayList<>();
                                    for (int col : unitsIndex) {
                                        for (Cell cell : grid.getCellsInUnitWithCandidate(digit, unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, col))
                                            if (!jellyFish.contains(cell) && cell.removeCandidate(digit)) {
                                                changed.add(cell);
                                                log("JellyFish %d in %s. Removed %d from %s%n", digit, jellyFish.stream().map(Cell::toString).toList(), digit, cell);
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
        }
        return List.of();
    }
}
