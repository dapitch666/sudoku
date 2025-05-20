package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;

import java.util.Arrays;
import java.util.List;

public class XYZWings extends SolvingTechnique {
    public XYZWings() {
        super("XYZ-Wings", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (int digit = 1; digit <= 9; digit++) {
            for (Cell hinge : grid.getCells(Predicates.containsCandidate(digit).and(Predicates.cellsWithNCandidates(3, 3)))) {
                int x = digit;
                Integer[] candidates = hinge.getCandidates().stream().filter(candidate -> candidate != x).toArray(Integer[]::new);
                int y = candidates[0], z = candidates[1];
                Cell[] peers = grid.getCells(Predicates.isPeerOf(hinge).and(Predicates.containsCandidate(digit)));
                if (peers.length < 2) continue;
                Cell wing1 = null, wing2 = null;
                for (Cell peer : peers) {
                    if (peer.isBiValue() && peer.hasCandidate(y)) {
                        wing1 = peer;
                    }
                }
                if (wing1 == null) continue;
                for (Cell peer : peers) {
                    if (peer.isBiValue() && peer.hasCandidate(z) && !peer.isPeer(wing1)) {
                        wing2 = peer;
                    }
                }
                if (wing2 == null) continue;
                List<Cell> changed = Arrays.stream(peers).filter(Predicates.isPeerOf(wing1).and(Predicates.isPeerOf(wing2))).toList();

                if (changed.isEmpty()) continue;
                for (Cell peer : changed) {
                    peer.removeCandidate(digit);
                }
                log("XYZ-Wing in %s (hinge), %s and %s {%d, %d, %d}%n- Removed candidate %d from %s%n",
                        hinge, wing1, wing2, x, y, z, digit, changed);
                incrementCounter();
                return changed;
            }
        }
        return List.of();
    }
}
