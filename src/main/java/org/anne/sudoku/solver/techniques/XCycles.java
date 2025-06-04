package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;
import org.anne.sudoku.model.Cycle;

import java.util.*;


public class XCycles extends SolvingTechnique {
    public XCycles() {
        super("X-Cycles", Grade.VERY_HARD);
    }

    private Grid grid;
    
    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        for (int digit = 1; digit <= 9; digit++) {
            // Find all cycles for the current digit
            // A cycle is a sequence of cells that alternate between strong and weak links
            // and form a closed loop.
            var strongLinks = grid.findLinks(digit, true);
            var weakLinks = grid.findLinks(digit, false);
            var cycles = new Graph<>(strongLinks, weakLinks).findAllCycles();
            for (Cycle<Cell> cycle : cycles) {
                // Classify the cycle
                Cycle.CycleType cycleType = cycle.getCycleType();
                // Apply the appropriate rule based on the cycle type
                Rule rule = switch (cycleType) {
                    case CONTINUOUS -> this::rule1;
                    case DISCONTINUOUS_STRONG -> this::rule2;
                    case DISCONTINUOUS_WEAK -> this::rule3;
                };
                var changed = rule.apply(digit, cycle);
                if (!changed.isEmpty()) {
                    incrementCounter();
                    log(0, "%s on %d (length %d) detected in %s%n", cycleType, digit, cycle.size(), cycle.stream().map(Cell::toString).toList());
                    return changed;
                }
            }
        }
        return List.of();
    }

    private List<Cell> rule1(int digit, Cycle<Cell> cycle) {
        List<Cell> changed = new ArrayList<>();
        for (int i = 0; i < cycle.size() - 1; i += 2) {
            changed.addAll(List.of(grid.getCells(
                    Predicates.isPeerOf(cycle.get(i))
                            .and(Predicates.isPeerOf(cycle.get(i + 1)))
                            .and(Predicates.containsCandidate(digit))
                            .and(cell -> !cycle.contains(cell)))));

        }
        if (changed.isEmpty()) return List.of();
        changed.forEach(cell -> cell.removeCandidate(digit));
        log("- Removed candidate {%d} from %s%n", digit, changed);
        return changed;
    }

    private List<Cell> rule2(int digit, Cycle<Cell> cycle) {
        // If the adjacent links are links with strong inference,
        // a candidate can be fixed in the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the last one.
        Cell cell = cycle.getLast();
        var removed = cell.removeAllBut(List.of(digit));
        if (removed.isEmpty()) return List.of();
        log("- Removed candidate(s) %s from %s%n", removed, cell);
        return List.of(cell);
    }

    private List<Cell> rule3(int digit, Cycle<Cell> cycle) {
        // If the adjacent links are links with weak inference (broken line),
        // the candidate can be eliminated from the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the first one.
        Cell cell = cycle.getFirst();
        cell.removeCandidate(digit);
        log("- Removed candidate {%d} from %s%n", digit, cell);
        return List.of(cell);
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(int digit, Cycle<Cell> cycle);
    }
}
