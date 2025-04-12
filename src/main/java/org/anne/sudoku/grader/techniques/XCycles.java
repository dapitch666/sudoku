package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.grader.*;

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
            var strongLinks = grid.findStrongLinks(digit);
            var weakLinks = grid.findWeakLinks(digit);
            var cycles = new Graph<>(strongLinks, weakLinks).findAllCycles();
            for (Cycle<Cell> cycle : cycles) {
                // Classify the cycle
                CycleType cycleType = classifyCycle(cycle, strongLinks);
                // Apply the appropriate rule based on the cycle type
                Rule rule = switch (cycleType) {
                    case CONTINUOUS -> this::rule1;
                    case DISCONTINUOUS_STRONG -> this::rule2;
                    case DISCONTINUOUS_WEAK -> this::rule3;
                };
                var changed = rule.apply(digit, cycle);
                if (!changed.isEmpty()) {
                    incrementCounter();
                    log(0, "%s on %d (length %d) detected in %s:%n", cycleType, digit, cycle.size(), cycle.stream().map(Cell::toString).toList());
                    return changed;
                }
            }
        }
        return List.of();
    }

    private List<Cell> rule3(int digit, Cycle<Cell> cycle) {
        // If the adjacent links are links with weak inference (broken line),
        // the candidate can be eliminated from the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the first one.
        Cell cell = cycle.getFirst();
        cell.removeCandidate(digit);
        log("Removed %d from %s%n", digit, cell);
        return List.of(cell);
    }

    private List<Cell> rule2(int digit, Cycle<Cell> cycle) {
        // If the adjacent links are links with strong inference,
        // a candidate can be fixed in the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the last one.
        Cell cell = cycle.getLast();
        var removed = cell.removeAllBut(digit);
        if (!removed.isEmpty()) {
            log("Removed %s from %s%n", removed, cell);
            return List.of(cell);
        }
        return List.of();
    }

    private List<Cell> rule1(int digit, Cycle<Cell> cycle) {
        List<Cell> changed = new ArrayList<>();
        for (int i = 0; i < cycle.size() - 1; i += 2) {
            for (Cell cell : grid.getCommonPeersWithCandidate(cycle.get(i), cycle.get(i + 1), digit)) {
                if (!cycle.contains(cell) && cell.removeCandidate(digit)) {
                    changed.add(cell);
                    log("Removed %d from %s%n", digit, cell);
                }
            }
        }
        return changed;
    }

    private CycleType classifyCycle(Cycle<Cell> cycle, Map<Cell, List<Cell>> strongLinks) {
        if (cycle.size() % 2 == 0) {
            return CycleType.CONTINUOUS;
        }
        if (!strongLinks.getOrDefault(cycle.getFirst(), List.of()).contains(cycle.getLast())) {
            return CycleType.DISCONTINUOUS_WEAK;
        }
        return CycleType.DISCONTINUOUS_STRONG;
    }

    private enum CycleType {
        CONTINUOUS,
        DISCONTINUOUS_STRONG,
        DISCONTINUOUS_WEAK;

        @Override
        public String toString() {
            return switch (this) {
                case CONTINUOUS -> "Continuous Alternating Nice Loop";
                case DISCONTINUOUS_STRONG -> "Discontinuous Alternating Nice Loop (Strong)";
                case DISCONTINUOUS_WEAK -> "Discontinuous Alternating Nice Loop (Weak)";
            };
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(int digit, Cycle<Cell> cycle);
    }
}
