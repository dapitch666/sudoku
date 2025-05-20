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

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        for (Cell cell : grid.getCells(Predicates.biValueCells)) {
            for (int digit : cell.getCandidates()) {
                Set<List<Cell>> chains = new HashSet<>();
                // Try to build chain from this cell
                findXYChain(digit, digit, new ArrayList<>(List.of(cell)), chains);
                if (chains.isEmpty()) continue;
                for (List<Cell> chain : chains) {
                    Cell cell1 = chain.getFirst();
                    Cell cell2 = chain.getLast();
                    List<Cell> changed = Arrays.stream(grid.getCells(Predicates.isPeerOf(cell1)
                            .and(Predicates.isPeerOf(cell2))
                            .and(Predicates.containsCandidate(digit))))
                            .toList();
                    if (changed.isEmpty()) continue;
                    log("XY Chain found for {%d}: %s%n", digit, chain);
                    return removeCandidateFromCellsAndLog(changed, digit);
                }
            }
        }
        return List.of();
    }

    private void findXYChain(int targetDigit, int currentDigit, List<Cell> currentChain, Set<List<Cell>> results) {
        Cell currentCell = currentChain.getLast();
        // Get the other digit in current bi-value cell
        int otherDigit = currentCell.candidates().stream().filter(c -> c != currentDigit).findFirst().orElseThrow();
        // If other digit is target digit, we closed the chain
        if (otherDigit == targetDigit && currentChain.size() > 2) {
            results.add(new ArrayList<>(currentChain));
        }

        // Look for next link in chain among isPeerOf
        for (Cell peer : grid.getCells(
                Predicates.isPeerOf(currentCell)
                        .and(Predicates.biValueCells)
                        .and(Predicates.containsCandidate(otherDigit)))
        ) {
            if (currentChain.contains(peer)) continue;
            currentChain.add(peer);
            findXYChain(targetDigit, otherDigit, currentChain, results);
            currentChain.removeLast();
        }
    }
}
