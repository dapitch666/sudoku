package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class NakedQuads implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, UnitType unitType, int unitIndex, StringBuilder sb) {
        Cell[] unit = grid.getCells(unitType, unitIndex);
        List<Cell> changed = new ArrayList<>();
        List<Cell> cells = Arrays.stream(unit).filter(cell -> cell.getCandidateCount() >= 2 && cell.getCandidateCount() <= 4).toList();
        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                for (int k = j + 1; k < cells.size(); k++) {
                    for (int l = k + 1; l < cells.size() ; l++) {
                        Set<Integer> quad = new HashSet<>();
                        quad.addAll(cells.get(i).getCandidates());
                        quad.addAll(cells.get(j).getCandidates());
                        quad.addAll(cells.get(k).getCandidates());
                        quad.addAll(cells.get(l).getCandidates());
                        if (quad.size() == 4) {
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
                                        sb.append(String.format("Naked quad in %s, %s, %s and %s. Removed %s from %s%n", cells.get(i).getPosition(), cells.get(j).getPosition(), cells.get(k).getPosition(), cells.get(l).getPosition(), removed, cell.getPosition()));
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
