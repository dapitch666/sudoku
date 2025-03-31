package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.ArrayList;
import java.util.List;

public class XyzWings implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
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
                            sb.append(String.format("XYZ-Wing in %s (hinge), %s and %s (%d, %d, %d). Removed %d from %s%n", hinge.getPosition(), wing1.getPosition(), wing2.getPosition(), x, y, z, x, peer.getPosition()));
                        }
                    }
                    if (!changed.isEmpty()) {
                        incrementCounter(counter);
                        return changed;
                    }
                }
            }
        }
        return List.of();
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
