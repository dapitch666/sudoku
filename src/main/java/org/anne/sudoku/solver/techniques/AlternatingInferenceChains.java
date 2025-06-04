package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;

public class AlternatingInferenceChains extends SolvingTechnique {

    public AlternatingInferenceChains() {
        super("Alternating Inference Chains", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        var strongLinks = findLinks(true);
        var weakLinks = findLinks(false);

        @SuppressWarnings("unchecked")
        var cycles = new Graph<>(strongLinks, weakLinks)
                .findAllCycles()
                .stream()
                .sorted(Comparator.comparingInt(cycle -> ((Cycle<Candidate>) cycle).size())
                        .thenComparing(cycle -> ((Cycle<Candidate>) cycle).getCycleType()))
                .toList();
        for (var cycle : cycles) {
            if (!isValid(cycle)) continue; // We need at least 4 cells to form a valid chain
            Cycle.CycleType cycleType = cycle.getCycleType();
            Rule rule = switch (cycleType) {
                case CONTINUOUS -> this::applyRule1;
                case DISCONTINUOUS_STRONG -> this::applyRule2;
                case DISCONTINUOUS_WEAK -> this::applyRule3;
            };
            var changed = rule.apply(cycle);
            if (!changed.isEmpty()) {
                incrementCounter();
                log(0, "%s cycle detected: %s%n", cycleType, cycle);
                return changed;
            }
        }
        return List.of();
    }

    /*
     * When a node has two strong links, the digits must be different
     * When a node has two weak links, the cell must be bivalue and the digits must be different
     * When a node has two different links (one weak, one strong), the digits must be the same
     */
    private boolean isValid(Cycle<Candidate> cycle) {
        if (cycle.size() < 4) return false;
        for (int i = 1; i < cycle.size(); i++) {
            Candidate node = cycle.get(i);
            Candidate c1 = cycle.get(i - 1);
            Candidate c2;
            if (i == cycle.size() - 1) {
                c2 = cycle.getFirst(); // Wrap around to the first cell
            } else {
                c2 = cycle.get(i + 1);
            }

        }
        return true;
    }

    private List<Cell> applyRule1(Cycle<Candidate> cycle) {
        List<Cell> changed = new ArrayList<>();
        for (int i = 0; i < cycle.size(); i++) {
            Candidate c1 = cycle.get(i);
            Candidate c2;
            if (i == cycle.size() - 1) {
                c2 = cycle.getFirst(); // Wrap around to the first cell
            } else {
                c2 = cycle.get(i + 1);
            }
            if (c1.cell.equals(c2.cell)) {
                Cell cell = c1.cell;
                BitSet toRemove = new BitSet(9);
                cell.candidates().stream()
                        .filter(digit -> !cycle.contains(new Candidate(cell, digit)))
                        .forEach(toRemove::set);
                if (toRemove.isEmpty()) continue;
                cell.removeCandidates(toRemove);
                changed.add(cell);
                log("- Removed candidate(s) %s from %s%n", toRemove, cell);
            }
            if (c1.digit != c2.digit) {
                continue;
            }
            int digit = c1.digit;
            for (Cell peer : grid.getCells(Predicates.isPeerOf(c1.cell)
                    .and(Predicates.isPeerOf(c2.cell))
                    .and(Predicates.containsCandidate(digit))
                    .and(cell -> !cycle.contains(new Candidate(cell, digit))))) {
                peer.removeCandidate(digit);
                changed.add(peer);
                log("- Removed candidate {%d} from %s%n", digit, peer);
            }
        }
        return changed;
    }

    private List<Cell> applyRule2(Cycle<Candidate> cycle) {
        Candidate candidate = cycle.getLast();
        var removed = candidate.cell.removeAllBut(List.of(candidate.digit));
        if (removed.isEmpty()) return List.of();
        log("- Removed candidate(s) %s from %s%n", removed, candidate.cell);
        return List.of(candidate.cell);
    }

    private List<Cell> applyRule3(Cycle<Candidate> cycle) {
        Cell cell = cycle.getFirst().cell;
        int digit = cycle.getFirst().digit;
        cell.removeCandidate(digit);
        log("- Removed candidate {%d} from %s%n", digit, cell);
        return List.of(cell);
    }

    private Map<Candidate, List<Candidate>> findLinks(boolean isStrong) {
        Map<Candidate, List<Candidate>> links = new java.util.HashMap<>();
        for (Cell cell : grid.getCells(Predicates.unsolvedCells)) {
            for (int digit : cell.getCandidates()) {
                Candidate candidate = new Candidate(cell, digit);
                List<Candidate> candidateLinks = new ArrayList<>();
                if (isStrong) {
                    if (cell.isBiValue()) {
                        candidateLinks.add(new Candidate(cell, cell.getOtherCandidate(digit)));
                    }
                } else {
                    cell.candidates().stream().filter(c -> c != digit).forEach(otherDigit -> candidateLinks.add(new Candidate(cell, otherDigit)));
                }
                var linkedCells = grid.findLinks(digit, isStrong);
                if (linkedCells.containsKey(cell)) {
                    candidateLinks.addAll(linkedCells.get(cell).stream().map(c -> new Candidate(c, digit)).toList());
                }
                if (!candidateLinks.isEmpty()) {
                    links.put(candidate, candidateLinks);
                }
            }
        }
        return links;
    }

    record Candidate(Cell cell, int digit) {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Candidate(Cell cell1, int digit1))) return false;
            return this.cell.equals(cell1) && this.digit == digit1;
        }

        @Override
        public int hashCode() {
            return this.cell.hashCode() * 31 + this.digit;
        }

        @Override
        public String toString() {
            return String.format("%s(%d)", cell, digit);
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(Cycle<Candidate> cycle);
    }
}
