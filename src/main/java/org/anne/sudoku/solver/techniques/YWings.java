package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YWings extends SolvingTechnique {
    public YWings() {
        super("Y-Wings", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (Cell key : grid.getCells(Predicates.cellsWithNCandidates(2))) {
            List<Integer> keyCandidates = key.getCandidates();
            for (Cell cell1 : grid.getCells(Predicates.peers(key))) {
                if (cell1.getCandidateCount() != 2) {
                    continue;
                }
                if (keyCandidates.stream().filter(cell1::hasCandidate).count() == 1) {
                    List<Cell> changed = new ArrayList<>();
                    int B = keyCandidates.stream().filter(cell1::hasCandidate).findFirst().orElseThrow();
                    int A = keyCandidates.stream().filter(candidate -> candidate != B).findFirst().orElseThrow();
                    int C = cell1.getCandidates().stream().filter(candidate -> candidate != B).findFirst().orElseThrow();
                    for (Cell cell2 : grid.getCells(Predicates.peers(key))) {
                        if (cell2.getCandidateCount() == 2 && cell2.hasCandidate(A) && cell2.hasCandidate(C)) {
                            for (Cell peer : grid.getCells(Predicates.peers(cell1).and(Predicates.peers(cell2)))) {
                                if (peer.removeCandidate(C)) {
                                    log("Y-Wing in %s, %s and %s, removed candidate %d from %s%n", key, cell1, cell2, C, peer);
                                    changed.add(peer);
                                }
                            }
                        }
                    }
                    if (!changed.isEmpty()) {
                        incrementCounter();
                        return changed;
                    }
                }
            }
        }
        return List.of();
    }
}
