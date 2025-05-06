package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class NakedTriples extends SolvingTechnique {
    public NakedTriples() {
        super("Naked Triples", Grade.EASY);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : UnitType.values()) {
            for (int unitIndex = 0; unitIndex < 9; unitIndex++) {
                Cell[] unit = grid.getCells(Predicates.inUnit(unitType, unitIndex));
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
                                if (!cell.isSolved() && cell != cells.get(i) && cell != cells.get(j) && cell != cells.get(k)) {
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
