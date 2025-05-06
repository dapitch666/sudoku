package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;

import java.util.*;

public class XYChains extends SolvingTechnique {
    public XYChains() {
        super("XY-Chains", Grade.VERY_HARD);
    }

    @Override
    public List<Cell> apply(Grid grid) {
        for (Cell cell : grid.getCells(Predicates.biValueCells)) {
            for (int digit : cell.getCandidates()) {
                Set<List<Cell>> chains = new HashSet<>();
                // Try to build chain from this cell
                findXYChain(grid, digit, digit, new ArrayList<>(List.of(cell)), chains);
                if (!chains.isEmpty()) {
                    for (List<Cell> chain : chains) {
                        if (chain.size() < 3 || !chain.getLast().hasCandidate(digit)) continue;
                        Cell cell1 = chain.getFirst();
                        Cell cell2 = chain.getLast();
                        List<Cell> peers = Arrays.stream(grid.getCells(Predicates.peers(cell1).and(Predicates.peers(cell2)).and(Predicates.hasCandidate(digit)))).toList();
                        if (peers.isEmpty()) continue;
                        log(0, "XY Chain found for %d: %s%n", digit, chain);
                        for (Cell c : peers) {
                            c.removeCandidate(digit);
                            log("%d removed from %s%n", digit, c);
                        }
                        incrementCounter();
                        return peers;
                    }
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
            results.add(new ArrayList<>(currentChain));
        }

        // Look for next link in chain among peers
        for (Cell peer : grid.getCells(Predicates.peers(currentCell))) {
            if (peer.isBiValue() && peer.hasCandidate(otherDigit) && !currentChain.contains(peer)) {
                currentChain.add(peer);
                findXYChain(grid, targetDigit, otherDigit, currentChain, results);
                currentChain.removeLast();
            }
        }
    }
}
