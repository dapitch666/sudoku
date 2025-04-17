package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.ArrayList;
import java.util.List;

public class NakedPairs extends SolvingTechnique {
    public NakedPairs() {
        super("Naked Pairs", Grade.VERY_EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
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
                                    log("Naked pair %s in %s, on cells [%s, %s]. Removed %s from %s%n", pair, unitType.toString(unitIndex), unit[i], unit[j], removed, cell);
                                }
                            }
                        }
                        if (!changed.isEmpty()) {
                            incrementCounter();
                        }
                    }
                }
            }
        }
        return changed;
    }
}
