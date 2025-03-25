package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class NakedTriples implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        Cell[] unit = grid.getCells(unitType, unitIndex);
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(unit).filter(cell -> cell.getCandidateCount() == 2 || cell.getCandidateCount() == 3).toList();
        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                for (int k = j + 1; k < cells.size(); k++) {
                    Set<Integer> triple = new HashSet<>();
                    triple.addAll(cells.get(i).getCandidates());
                    triple.addAll(cells.get(j).getCandidates());
                    triple.addAll(cells.get(k).getCandidates());
                    if (triple.size() == 3) {
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
                                    sb.append(String.format("Naked triple %s in %s, on cells [%s, %s, %s]. Removed %s from %s%n", triple, unitType.toString(unitIndex), cells.get(i).getPosition(), cells.get(j).getPosition(), cells.get(k).getPosition(), removed, cell.getPosition()));
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
