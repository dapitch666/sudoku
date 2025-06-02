package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Predicates;

import java.util.*;
import java.util.stream.Collectors;


public class ThreeDMedusa extends SolvingTechnique {
    public ThreeDMedusa() {
        super("3D Medusa", Grade.VERY_HARD);
    }

    // Color definitions for coloring
    private static final int NO_COLOR = 0;
    private static final int GREEN = 1;
    private static final int YELLOW = 2;

    private Set<ColoredCandidate> coloredCandidates;
    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rule> rules = List.of(this::rule1, this::rule2, this::rule3, this::rule4, this::rule5, this::rule6);

        // Find all candidates that can be connected through bi-value cells or bi-location
        for (Cell startCell : grid.getCells(Predicates.unsolvedCells)) {
            for (int candidate : startCell.getCandidates()) {
                // Reset the color network for each new attempt
                coloredCandidates = new HashSet<>();

                if (!buildColoringNetwork(startCell, candidate)) continue;
                for (Rule rule : rules) {
                    List<Cell> changed = rule.apply();
                    if (changed.isEmpty()) continue;
                    incrementCounter();
                    return changed;
                }
            }
        }
        return List.of();
    }

    /**
     * Builds a network of colored candidates by alternating colors
     */
    private boolean buildColoringNetwork(Cell startCell, int startCandidate) {
        ColoredCandidate start = new ColoredCandidate(startCell, startCandidate, GREEN);
        coloredCandidates.add(start);
        Queue<ColoredCandidate> queue = new LinkedList<>(List.of(start));
        Set<ColoredCandidate> visited = new HashSet<>(List.of(start));

        if (startCell.isBiValue()) {
            int otherCandidate = startCell.getOtherCandidate(startCandidate);
            ColoredCandidate cc = new ColoredCandidate(startCell, otherCandidate, YELLOW);
            coloredCandidates.add(cc);
            visited.add(cc);
            queue.offer(cc);
        }

        while (!queue.isEmpty()) {
            ColoredCandidate current = queue.poll();
            int oppositeColor = (current.color == GREEN) ? YELLOW : GREEN;

            // Look for bi-location connections
            for (Cell peer : grid.getCells(Predicates.isPeerOf(current.cell)
                    .and(Predicates.unsolvedCells)
                    .and(Predicates.containsCandidate(current.candidate))
                    .and(cell -> grid.isConjugatePair(current.cell, cell, current.candidate)))) {

                ColoredCandidate newCC = new ColoredCandidate(peer, current.candidate, oppositeColor);
                if (visited.contains(newCC) || isAlreadyColored(newCC)) continue;
                coloredCandidates.add(newCC);
                queue.offer(newCC);
                if (getColor(peer, current.candidate) == current.color) {
                    // Contradiction found: same candidate, same color in a unit
                    return true;
                }
            }

            // Look for bi-value connections (different candidate in the same cell)
            if (current.cell.isBiValue()) {
                int otherCandidate = current.cell.getOtherCandidate(current.candidate);

                ColoredCandidate newCC = new ColoredCandidate(current.cell, otherCandidate, oppositeColor);
                if (visited.contains(newCC) || isAlreadyColored(newCC)) continue;
                coloredCandidates.add(newCC);
                queue.offer(newCC);
                if (getColor(current.cell, otherCandidate) == current.color) {
                    // Contradiction found: two candidates of the same color in a cell
                    return true;
                }
            }
        }
        return !coloredCandidates.isEmpty();
    }

    /**
     * Rule 1: If two candidates in a cell have the same color,
     * all occurrences of that color can be removed
     */
    private List<Cell> rule1() {
        for (Cell cell : coloredCandidates.stream().map(cc -> cc.cell).distinct().toList()) {
            Optional<Map.Entry<Integer, Long>> colorCount = cell.candidates().stream().boxed()
                    .map(candidate -> getColor(cell, candidate))
                    .filter(color -> color != NO_COLOR)
                    .collect(Collectors.groupingBy(color -> color, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .findFirst();

            if (colorCount.isEmpty()) continue;
            int color = colorCount.get().getKey();
            log("Rule 1: %s has %d candidates of the same color. All candidates with this color can be removed%n", cell, color);
            return eliminateColor(color);
        }
        return List.of();
    }

    /**
     * Rule 2: If two occurrences of the same candidate with the same color
     * appear in the same unit, that color can be eliminated
     */
    private List<Cell> rule2() {
        for (ColoredCandidate coloredCandidate : coloredCandidates) {
            List<ColoredCandidate> sameColorCandidates = coloredCandidates.stream()
                    .filter(cc -> cc.color == coloredCandidate.color
                            && cc.candidate == coloredCandidate.candidate
                            && coloredCandidate.cell.isPeer(cc.cell))
                    .toList();
            if (sameColorCandidates.isEmpty()) continue;
            log("Rule 2: Candidate %d in %s and %s have the same color. All candidates with this color can be removed%n", coloredCandidate.candidate, sameColorCandidates.getFirst().cell, coloredCandidate.cell);
            return eliminateColor(coloredCandidate.color);
        }
        return new ArrayList<>();
    }

    /**
     * Rule 3: If an uncolored candidate can see two candidates of opposite colors
     * in its own cell, it can be eliminated
     */
    private List<Cell> rule3() {
        for (Cell cell : coloredCandidates.stream()
                .map(ColoredCandidate::cell)
                .distinct()
                .filter(c -> c.getCandidateCount() > 2)
                .toList()) {

            List<Integer> candidates = coloredCandidates.stream()
                    .filter(cc -> cc.cell.equals(cell))
                    .map(ColoredCandidate::candidate)
                    .toList();
            if (candidates.size() == 2) { // same color candidate has already been dealt with on Rule 1
                BitSet removed = cell.removeAllBut(candidates);
                if (removed.isEmpty()) continue;
                log("Rule 3: 2 colors appear in cell %s. Removing the uncolored candidates%n- Removed %s from %s%n", cell, removed, cell);
                return List.of(cell);
            }
        }
        return List.of();
    }

    /**
     * Rule 4: If an uncolored candidate can see two candidates of opposite colors
     * in the same unit, it can be eliminated
     */
    private List<Cell> rule4() {
        List<ColoredCandidate> candidatesToRemove = new ArrayList<>();
        for (ColoredCandidate coloredCandidate : coloredCandidates) {
            Cell cell = coloredCandidate.cell;
            int candidate = coloredCandidate.candidate;
            int color = coloredCandidate.color;

            for (ColoredCandidate other : coloredCandidates.stream()
                    .filter(cc -> cc.candidate == candidate && cc.color != color && cc.cell != cell)
                    .toList()) {

                for (Cell peer : grid.getCells(Predicates.isPeerOf(cell).and(Predicates.isPeerOf(other.cell)).and(Predicates.containsCandidate(candidate)))) {
                    if (candidatesToRemove.stream().anyMatch(cc -> cc.cell() == peer && cc.candidate() == candidate)) {
                        continue; // Ignore candidates that are already marked for removal
                    }
                    if (getColor(peer, candidate) == NO_COLOR) {
                        log("Rule 4: Uncolored candidate %d in %s can see two different candidates %d elsewhere (%s and %s)%n", candidate, peer, candidate, cell, other.cell);
                        log("- Removed candidate %d from %s%n", candidate, peer);
                        peer.removeCandidate(candidate);
                        candidatesToRemove.add(new ColoredCandidate(peer, candidate, NO_COLOR));
                        break;
                    }
                }
            }
        }
        return candidatesToRemove.stream().map(ColoredCandidate::cell).distinct().toList();
    }

    /**
     * Rule 5: If an uncolored candidate can see a colored candidate in a unit
     * and an opposite colored candidate in its own cell, it can be eliminated
     */
    private List<Cell> rule5() {
        List<ColoredCandidate> candidatesToRemove = new ArrayList<>();
        for (ColoredCandidate coloredCandidate : coloredCandidates) {
            Cell cell = coloredCandidate.cell;
            // Ignore cells with multiple colored candidates
            if (coloredCandidates.stream().filter(cc -> cc.cell == cell).count() > 1) continue;

            int oppositeColor = coloredCandidate.color == GREEN ? YELLOW : GREEN;
            for (int candidate : cell.candidates().stream().filter(cc -> cc != coloredCandidate.candidate).boxed().toList()) {
                for (Cell c : grid.getCells(Predicates.isPeerOf(cell).and(Predicates.containsCandidate(candidate))
                        .and(c -> getColor(c, candidate) == oppositeColor))) {
                    log("Rule 5: Uncolored candidate %d in %s can see a colored %d elsewhere (%s) and an oppositely colored %d in its own cell%n", candidate, cell, candidate, c, coloredCandidate.candidate);
                    log("- Removed candidate %d from %s%n", candidate, cell);
                    cell.removeCandidate(candidate);
                    candidatesToRemove.add(new ColoredCandidate(cell, candidate, NO_COLOR));
                    break;
                }
            }
        }
        return candidatesToRemove.stream().map(ColoredCandidate::cell).distinct().toList();
    }

    /**
     * Rule 6: If all candidates in a cell with no colored candidates can see the same color,
     * the cell would be emptied if that color was correct, so the opposite color must be correct
     */
    private List<Cell> rule6() {
        for (Cell cell : grid.getCells(Predicates.unsolvedCells.and(cell -> !hasColoredCandidates(cell)))) {
            Map<Integer, Set<Integer>> colorCount = new HashMap<>();
            for (int candidate : cell.getCandidates()) {
                for (Cell peer : grid.getCells(c -> c.isPeer(cell) && c.hasCandidate(candidate))) {
                    int color = getColor(peer, candidate);
                    if (color != NO_COLOR) {
                        colorCount.putIfAbsent(color, new HashSet<>());
                        colorCount.get(color).add(candidate);
                    }
                }
            }
            if (colorCount.size() == 1) {
                int color = colorCount.keySet().iterator().next();
                if (colorCount.get(color).size() == cell.getCandidateCount()) {
                    log("Rule 6: All candidates %s in %s are uncolored and can see the same color. That would empty the cell so all candidates of that colour can be removed.%n", cell.getCandidates(), cell);
                    return eliminateColor(color);
                }
            }
        }
        return List.of();
    }

    private boolean hasColoredCandidates(Cell cell) {
        return coloredCandidates.stream().anyMatch(cc -> cc.cell.equals(cell));
    }

    /**
     * Eliminates all candidates of a specified color and solves the candidates of the opposite color
     */
    private List<Cell> eliminateColor(int colorToEliminate) {
        List<Cell> changed = new ArrayList<>();

        // Eliminate all candidates of the specified color
        for (ColoredCandidate cc : coloredCandidates.stream().filter(cc -> cc.color == colorToEliminate).toList()) {
            cc.cell.removeCandidate(cc.candidate);
            log("- Removed candidate {%d} from %s%n", cc.candidate, cc.cell);
            changed.add(cc.cell);
        }
        return changed;
    }

    /**
     * Checks if a candidate is already colored
     */
    private boolean isAlreadyColored(ColoredCandidate cc) {
        for (ColoredCandidate existing : coloredCandidates) {
            if (existing.cell.equals(cc.cell) && existing.candidate == cc.candidate) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the color of a specific candidate in a cell
     */
    private int getColor(Cell cell, int candidate) {
        for (ColoredCandidate cc : coloredCandidates) {
            if (cc.cell.equals(cell) && cc.candidate == candidate) {
                return cc.color;
            }
        }
        return NO_COLOR;
    }

    // Structure to store colored candidates
    record ColoredCandidate(Cell cell, int candidate, int color) {

        @Override
        public String toString() {
            String color = switch (this.color) {
                case GREEN -> "GREEN";
                case YELLOW -> "YELLOW";
                default -> "NO_COLOR";
            };
            return cell + " (" + candidate + ", " + color + ")";
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply();
    }
}