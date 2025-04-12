package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class NakedQuads extends SolvingTechnique {
    public NakedQuads() {
        super("Naked Quads", Grade.MODERATE);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] unit = grid.getCells(unitType, unitIndex);
                List<Cell> cells = Arrays.stream(unit).filter(cell -> cell.getCandidateCount() >= 2 && cell.getCandidateCount() <= 4).toList();
                for (int i = 0; i < cells.size(); i++) {
                    for (int j = i + 1; j < cells.size(); j++) {
                        for (int k = j + 1; k < cells.size(); k++) {
                            for (int l = k + 1; l < cells.size(); l++) {
                                Set<Integer> quad = new HashSet<>();
                                quad.addAll(cells.get(i).getCandidates());
                                quad.addAll(cells.get(j).getCandidates());
                                quad.addAll(cells.get(k).getCandidates());
                                quad.addAll(cells.get(l).getCandidates());
                                if (quad.size() != 4) {
                                    continue;
                                }
                                for (Cell cell : unit) {
                                    if (cell.isNotSolved() && cell != cells.get(i) && cell != cells.get(j) && cell != cells.get(k) && cell != cells.get(l)) {
                                        List<Integer> removed = new ArrayList<>();
                                        for (int candidate : quad) {
                                            if (cell.removeCandidate(candidate)) {
                                                removed.add(candidate);
                                            }
                                        }
                                        if (!removed.isEmpty()) {
                                            changed.add(cell);
                                            log("Naked quad in %s, %s, %s and %s. Removed %s from %s%n", cells.get(i), cells.get(j), cells.get(k), cells.get(l), removed, cell);
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
        }
        return changed;
    }
}
