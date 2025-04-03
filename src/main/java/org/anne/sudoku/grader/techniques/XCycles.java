package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.*;

import java.util.*;

public class XCycles implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
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
                 var changed = switch (cycleType) {
                    case CONTINUOUS -> applyRule1(grid, digit, cycle, sb);
                    case DISCONTINUOUS_STRONG -> applyRule2(digit, cycle, sb);
                    case DISCONTINUOUS_WEAK -> applyRule3(digit, cycle, sb);
                 };
                if (!changed.isEmpty()) {
                    incrementCounter(counter);
                    log(sb, 0, "%s on %d (length %d) detected in %s:%n", cycleType, digit, cycle.size(), cycle.stream().map(Cell::getPosition).toList());
                    return changed;
                }
            }
        }
        return List.of();
    }

    private List<Cell> applyRule3(int digit, Cycle<Cell> cycle, StringBuilder sb) {
        // If the adjacent links are links with weak inference (broken line),
        // the candidate can be eliminated from the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the first one.
        Cell cell = cycle.getFirst();
        cell.removeCandidate(digit);
        log(sb, "Removed %d from %s%n", digit, cell.getPosition());
        return List.of(cell);
    }

    private List<Cell> applyRule2(int digit, Cycle<Cell> cycle, StringBuilder sb) {
        // If the adjacent links are links with strong inference,
        // a candidate can be fixed in the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the last one.
        Cell cell = cycle.getLast();
        var removed = cell.removeAllBut(digit);
        if (!removed.isEmpty()) {
            log(sb, "Removed %s from %s%n", removed, cell.getPosition());
            return List.of(cell);
        }
        return List.of();
    }

    private List<Cell> applyRule1(Grid grid, int digit, Cycle<Cell> cycle, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (int i = 0; i < cycle.size() - 1; i += 2) {
            for (Cell cell : grid.getCommonPeersWithCandidate(cycle.get(i), cycle.get(i + 1), digit)) {
                if (!cycle.contains(cell) && cell.removeCandidate(digit)) {
                    changed.add(cell);
                    log(sb, "Removed %d from %s%n", digit, cell.getPosition());
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

    @Override
    public int getCounter() {
        return counter[0];
    }
}
