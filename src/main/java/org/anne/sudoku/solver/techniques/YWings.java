package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class YWings extends SolvingTechnique {
    public YWings() {
        super("Y-Wings", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (Cell hinge : grid.getCells(Predicates.biValueCells)) {
            BitSet keyCandidates = hinge.candidates();
            for (Cell cell1 : grid.getCells(Predicates.peers(hinge).and(Predicates.biValueCells))) {
                if (keyCandidates.stream().filter(cell1::hasCandidate).count() == 1) {
                    List<Cell> changed = new ArrayList<>();
                    int b = keyCandidates.stream().filter(cell1::hasCandidate).findFirst().orElseThrow();
                    int a = keyCandidates.stream().filter(candidate -> candidate != b).findFirst().orElseThrow();
                    int c = cell1.getCandidates().stream().filter(candidate -> candidate != b).findFirst().orElseThrow();
                    for (Cell cell2 : grid.getCells(Predicates.peers(hinge)
                            .and(Predicates.biValueCells)
                            .and(Predicates.hasCandidates(List.of(a, c))))) {
                        for (Cell peer : grid.getCells(Predicates.peers(cell1).and(Predicates.peers(cell2)).and(Predicates.hasCandidate(c)))) {
                            peer.removeCandidate(c);
                            changed.add(peer);
                        }
                        if (!changed.isEmpty()) {
                            incrementCounter();
                            log(0, "Y-Wing on %s (hinge), %s and %s%n- Removed candidate %d from %s%n", hinge, cell1, cell2, c, changed);
                            return changed;
                        }
                    }
                }
            }
        }
        return List.of();
    }
}
