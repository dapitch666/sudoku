package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class NakedTriples extends SolvingTechnique {
    public NakedTriples() {
        super("Naked Triples");
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] unit = grid.getCells(unitType, unitIndex);
                List<Cell> cells = Arrays.stream(unit).filter(cell -> cell.getCandidateCount() == 2 || cell.getCandidateCount() == 3).toList();
                for (int i = 0; i < cells.size(); i++) {
                    for (int j = i + 1; j < cells.size(); j++) {
                        for (int k = j + 1; k < cells.size(); k++) {
                            Set<Integer> triple = new HashSet<>();
                            triple.addAll(cells.get(i).getCandidates());
                            triple.addAll(cells.get(j).getCandidates());
                            triple.addAll(cells.get(k).getCandidates());
                            if (triple.size() != 3) {
                                continue;
                            }
                            for (Cell cell : unit) {
                                if (cell.isNotSolved() && cell != cells.get(i) && cell != cells.get(j) && cell != cells.get(k)) {
                                    List<Integer> removed = new ArrayList<>();
                                    for (int candidate : triple) {
                                        if (cell.removeCandidate(candidate)) {
                                            removed.add(candidate);
                                        }
                                    }
                                    if (!removed.isEmpty()) {
                                        changed.add(cell);
                                        log("Naked triple %s in %s, on cells [%s, %s, %s]. Removed %s from %s%n", triple, unitType.toString(unitIndex), cells.get(i), cells.get(j), cells.get(k), removed, cell);
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
        }
        return changed;
    }
}
