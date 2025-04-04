package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Cycle;
import org.anne.sudoku.grader.Grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XYChains implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        for (int digit = 1; digit <= 9; digit++) {
            Set<List<Cell>> chains = new HashSet<>();
            for (Cell startCell : grid.getBiValueCellsWithCandidate(digit)) {
                // Try to build chain from this cell
                List<Cell> chain = new ArrayList<>(List.of(startCell));
                findXYChain(grid, digit, digit, chain, chains);
            }
            if (!chains.isEmpty()) {
                for (List<Cell> chain : chains) {
                    if (chain.size() < 3 || !chain.getLast().isCandidate(digit)) continue;
                    // if (chain.getFirst().isPeer(chain.getLast())) continue;
                    Cell cell1 = chain.getFirst();
                    Cell cell2 = chain.getLast();
                    List<Cell> peers = grid.getCommonPeersWithCandidate(cell1, cell2, digit);
                    if (peers.isEmpty()) continue;
                    log(sb, 0, "XY Chain found for %d: %s%n", digit, chain);
                    for (Cell c : peers) {
                        c.removeCandidate(digit);
                        log(sb, "%d removed from %s%n", digit, c);
                    }
                    incrementCounter(counter);
                    return peers;
                }
            }
        }
        return List.of();
    }

    private void findXYChain(Grid grid, int targetDigit, int currentDigit,
                             List<Cell> currentChain, Set<List<Cell>> results) {
        if (currentChain.isEmpty()) return;

        Cell currentCell = currentChain.getLast();
        // Get the other digit in current bivalue cell
        int otherDigit = currentCell.getCandidates().stream().filter(c -> c != currentDigit).findFirst().orElseThrow();
        // If other digit is target digit, we found a chain
        if (otherDigit == targetDigit) {
            results.add(new Cycle<>(currentChain));
        }
        // TODO: See why we seem to miss equal candidates cells (e.g. 1,2 and 2,1)
        // TODO: See why we seem to miss cycles
        // Look for next link in chain among peers
        for (Cell peer : grid.getPeers(currentCell)) {
            if (peer.isBiValue() && peer.isCandidate(otherDigit) && !currentChain.contains(peer)) {
                currentChain.add(peer);
                findXYChain(grid, targetDigit, otherDigit, currentChain, results);
                currentChain.removeLast();
            } else if (otherDigit == targetDigit) {
                results.add(new ArrayList<>(currentChain));
                return;
            }
        }
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
