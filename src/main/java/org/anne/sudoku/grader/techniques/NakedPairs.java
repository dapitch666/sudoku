package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.List;

public class NakedPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] unit = grid.getCells(unitType, unitIndex);

                for (int i = 0; i < 9; i++) {
                    if (unit[i].getCandidateCount() != 2) {
                        continue;
                    }
                    for (int j = i + 1; j < 9; j++) {
                        if (unit[j].getCandidateCount() != 2 || !unit[i].getCandidates().equals(unit[j].getCandidates())) {
                            continue;
                        }
                        List<Integer> pair = unit[i].getCandidates();
                        for (Cell cell : unit) {
                            if (cell != unit[i] && cell != unit[j]) {
                                List<Integer> removed = new ArrayList<>();
                                for (int candidate : pair) {
                                    if (cell.removeCandidate(candidate)) {
                                        removed.add(candidate);
                                    }
                                }
                                if (!removed.isEmpty()) {
                                    changed.add(cell);
                                    log(sb, "Naked pair %s in %s, on cells [%s, %s]. Removed %s from %s%n", pair, unitType.toString(unitIndex), unit[i].getPosition(), unit[j].getPosition(), removed, cell.getPosition());
                                }
                            }
                        }
                        if (!changed.isEmpty()) {
                            incrementCounter(counter);
                        }
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
