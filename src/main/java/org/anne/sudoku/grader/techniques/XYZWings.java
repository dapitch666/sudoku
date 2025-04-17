package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;

import java.util.ArrayList;
import java.util.List;

public class XYZWings extends SolvingTechnique {
    public XYZWings() {
        super("XYZ-Wings", Grade.HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (int digit = 1; digit <= 9; digit++) {
            for (Cell hinge : grid.getCellsWithCandidate(digit)) {
                if (hinge.getCandidates().size() == 3) {
                    int x = digit;
                    Integer[] candidates = hinge.getCandidates().stream().filter(candidate -> candidate != x).toArray(Integer[]::new);
                    int y = candidates[0];
                    int z = candidates[1];
                    Cell[] peers = grid.getPeers(hinge);
                    Cell wing1 = null, wing2 = null;
                    for (Cell peer : peers) {
                        if (peer.getCandidates().size() == 2 && peer.isCandidate(x) && peer.isCandidate(y)) {
                            wing1 = peer;
                        }
                    }
                    if (wing1 == null) continue;
                    for (Cell peer : peers) {
                        if (peer.getCandidates().size() == 2 && peer.isCandidate(x) && peer.isCandidate(z) && !peer.isPeer(wing1)) {
                            wing2 = peer;
                        }
                    }
                    if (wing2 == null) continue;
                    List<Cell> changed = new ArrayList<>();
                    for (Cell peer : peers) {
                        if (peer.isCandidate(x) && peer.isPeer(wing1) && peer.isPeer(wing2)) {
                            changed.add(peer);
                            peer.removeCandidate(x);
                            log("XYZ-Wing in %s (hinge), %s and %s (%d, %d, %d). Removed %d from %s%n", hinge, wing1, wing2, x, y, z, x, peer);
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
