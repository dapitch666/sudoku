package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.List;

public class NakedPairs implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        Cell[] unit = grid.getCells(unitType, unitIndex);
        List<Cell> changed = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (unit[i].getCandidateCount() == 2) {
                for (int j = i + 1; j < 9; j++) {
                    if (unit[j].getCandidateCount() == 2 && unit[i].getCandidates().equals(unit[j].getCandidates())) {
                        List<Integer> pair = unit[i].getCandidates();
                        for (int k = 0; k < 9; k++) {
                            if (k != i && k != j) {
                                List<Integer> removed = new ArrayList<>();
                                for (int candidate : pair) {
                                    if (unit[k].removeCandidate(candidate)) {
                                        removed.add(candidate);
                                    }
                                }
                                if (!removed.isEmpty()) {
                                    changed.add(unit[k]);
                                    sb.append(String.format("Naked pair %s in %s, on cells [%s, %s]. Removed %s from %s%n", pair, unitType.toString(unitIndex), unit[i].getPosition(), unit[j].getPosition(), removed, unit[k].getPosition()));
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
        if (!changed.isEmpty()) {
            log(sb.toString());
        }
        return changed;
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
