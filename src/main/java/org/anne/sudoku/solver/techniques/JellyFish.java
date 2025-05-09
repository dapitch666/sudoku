package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
                    Cell[] cellsWithCandidateInUnit = grid.getCells(Predicates.inUnit(unitType, unitIndex)
                            .and(Predicates.hasCandidate(digit)));
                    if (cellsWithCandidateInUnit.length >= 2 && cellsWithCandidateInUnit.length <= 4) {
                        list.add(cellsWithCandidateInUnit);
                    }
                }
                // Find aligned cells
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        for (int k = j + 1; k < list.size(); k++) {
                            for (int l = k + 1; l < list.size(); l++) {
                                List<Cell> jellyFish = Stream.of(list.get(i), list.get(j), list.get(k), list.get(l))
                                        .flatMap(Arrays::stream)
                                        .toList();
                                List<Integer> unitsIndex = jellyFish.stream()
                                        .map(cell -> unitType == UnitType.ROW ? cell.getCol() : cell.getRow())
                                        .distinct()
                                        .toList();

                                if (unitsIndex.size() != 4) continue;
                                List<Cell> changed = new ArrayList<>();
                                for (int unitIndex : unitsIndex) {
                                    changed.addAll(Arrays.asList(grid.getCells(Predicates.inUnit(unitType == UnitType.ROW ? UnitType.COL : UnitType.ROW, unitIndex)
                                            .and(Predicates.hasCandidate(digit))
                                            .and(cell -> !jellyFish.contains(cell)))));
                                }
                                if (changed.isEmpty()) continue;
                                log("JellyFish %d in %s%n", digit, jellyFish);
                                removeCandidateFromCellsAndLog(changed, digit);
                                return changed;
                            }
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
