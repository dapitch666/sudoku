package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;
import java.util.function.Predicate;

import static org.anne.sudoku.model.UnitType.*;

public class AlternatingInferenceChains extends SolvingTechnique {

    public AlternatingInferenceChains() {
        super("Alternating Inference Chains", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Cycle<Candidate>> cycles = new Graph<>(findLinks(true), findLinks(false))
                .findAllCycles()
                .stream()
                .filter(this::hasNoDuplicatedCells) // Skip cycles where the same cells appear multiple times
                .sorted(Comparator.<Cycle<Candidate>, Cycle.CycleType>comparing(Cycle::getCycleType)
                        .thenComparingInt(Cycle::size)
                        .thenComparing(this::withGroups))
                .toList();
        for (var cycle : cycles) {

            var cycleType = cycle.getCycleType();
            var changed = switch (cycleType) {
                case CONTINUOUS -> applyRule1(cycle);
                case DISCONTINUOUS_STRONG -> applyRule2(cycle);
                case DISCONTINUOUS_WEAK -> applyRule3(cycle);
            };

            if (!changed.isEmpty()) {
                incrementCounter();
                log(0, "%s cycle%s detected: %s%n", cycleType, withGroups(cycle) ? " (with groups)" : "", cycle);
                return changed;
            }
        }
        return List.of();
    }

    private boolean hasNoDuplicatedCells(Cycle<Candidate> cycle) {
        Set<String> seen = new HashSet<>();
        return cycle.stream()
                .allMatch(candidate -> seen.add(candidate.cells + ":" + candidate.digit));
    }

    private List<Cell> applyRule1(Cycle<Candidate> cycle) {
        Map<Cell, BitSet> candidateMap = new HashMap<>();
        cycle.forEach(candidate -> {
            for (Cell cell : candidate.cells.cells()) {
                candidateMap.computeIfAbsent(cell, _ -> new BitSet(9)).set(candidate.digit);
            }
        });

        List<Cell> changed = new ArrayList<>();
        for (int i = 0; i < cycle.size(); i++) {
            Candidate c1 = cycle.get(i);
            Candidate c2 = (i == cycle.size() - 1) ? cycle.getFirst() : cycle.get(i + 1);

            if (c1.cells.equals(c2.cells)) {
                Cell cell = c1.firstCell();
                BitSet removed = cell.removeAllBut(candidateMap.get(cell));
                if (!removed.isEmpty()) {
                    changed.add(cell);
                    log("- Removed candidate(s) %s from %s%n", removed, cell);
                }
            }

            if (c1.digit != c2.digit) continue;

            int digit = c1.digit;
            for (Cell peer : grid.getCells(Predicates.isPeerOf(c1.cells().cells())
                    .and(Predicates.isPeerOf(c2.cells().cells()))
                    .and(Predicates.containsCandidate(digit))
                    .and(cell -> !cycle.contains(new Candidate(new GroupedCell(cell), digit))))) {
                peer.removeCandidate(digit);
                changed.add(peer);
                log("- Removed candidate {%d} from %s%n", digit, peer);
            }
        }
        return changed;
    }

    private List<Cell> applyRule2(Cycle<Candidate> cycle) {
        if (!cycle.getLast().cells.isSingleCell()) return List.of();
        Cell cell = cycle.getLast().firstCell();
        int digit = cycle.getLast().digit;
        var removed = cell.removeAllBut(List.of(digit));
        if (removed.isEmpty()) return List.of();
        log("- Removed candidate(s) %s from %s%n", removed, cell);
        return List.of(cell);
    }

    private List<Cell> applyRule3(Cycle<Candidate> cycle) {
        if (!cycle.getFirst().cells.isSingleCell()) return List.of();
        Cell cell = cycle.getFirst().firstCell();
        int digit = cycle.getFirst().digit;
        cell.removeCandidate(digit);
        log("- Removed candidate {%d} from %s%n", digit, cell);
        return List.of(cell);
    }

    private Map<Candidate, List<Candidate>> findLinks(boolean isStrong) {
        Map<Candidate, List<Candidate>> links = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            int digit = i;
            Set<GroupedCell> groupedCells = getGroupedCells(digit);
            for (GroupedCell groupedCell : groupedCells) {
                List<Candidate> peers = new ArrayList<>();
                if (groupedCell.isSingleCell()) {
                    Cell cell = groupedCell.cells()[0];
                    if (isStrong) {
                        if (cell.isBiValue()) {
                            peers.add(new Candidate(groupedCell, cell.getOtherCandidate(digit)));
                        }
                    } else {
                        cell.candidates().stream()
                                .filter(candidate -> candidate != digit)
                                .mapToObj(otherDigit -> new Candidate(groupedCell, otherDigit))
                                .forEach(peers::add);
                    }
                }
                peers.addAll(groupedCells.stream()
                        .filter(gc -> !gc.equals(groupedCell) && !groupedCell.intersects(gc) &&
                                Arrays.stream(gc.cells()).allMatch(cell -> Arrays.stream(groupedCell.cells()).allMatch(cell::isPeer)))
                        .filter(gc -> !isStrong || isConjugatePair(groupedCell, gc, digit))
                        .map(gc -> new Candidate(gc, digit))
                        .toList());

                if (!peers.isEmpty()) {
                    links.put(new Candidate(groupedCell, digit), peers);
                }
            }
        }
        // Add ALS links
        if (isStrong) {
            for (Cell cell1 : grid.getCells(Predicates.biValueCells)) {
                for (Cell cell2 : grid.getCells(Predicates.isPeerOf(cell1)
                        .and(Predicates.containsAllCandidates(cell1.candidates()))
                        .and(cell -> cell.getCandidateCount() == 3))) {
                    BitSet extraCandidates = (BitSet) cell2.candidates().clone();
                    extraCandidates.andNot(cell1.candidates());
                    int digit = extraCandidates.nextSetBit(0);
                    Candidate candidate1 = new Candidate(new GroupedCell(cell2), digit);
                    Candidate candidate2 = new Candidate(new GroupedCell(cell1, cell2), cell1.getFirstCandidate());
                    Candidate candidate3 = new Candidate(new GroupedCell(cell1, cell2), cell1.getOtherCandidate(cell1.getFirstCandidate()));
                    if (links.containsKey(candidate1)) {
                        List<Candidate> existingLinks = new ArrayList<>(links.get(candidate1));
                        existingLinks.add(candidate2);
                        existingLinks.add(candidate3);
                        links.put(candidate1, existingLinks);
                    } else {
                        links.put(candidate1, List.of(candidate2, candidate3));
                    }
                    if (links.containsKey(candidate2)) {
                        List<Candidate> existingLinks = new ArrayList<>(links.get(candidate2));
                        existingLinks.add(candidate1);
                        links.put(candidate2, existingLinks);
                    } else {
                        links.put(candidate2, List.of(candidate1));
                    }
                    if (links.containsKey(candidate3)) {
                        List<Candidate> existingLinks = new ArrayList<>(links.get(candidate3));
                        existingLinks.add(candidate1);
                        links.put(candidate3, existingLinks);
                    } else {
                        links.put(candidate3, List.of(candidate1));
                    }
                }
            }
        }
        return links;
    }

    private Set<GroupedCell> getGroupedCells(int digit) {
        Set<GroupedCell> groupedCellSet = new HashSet<>();
        for (Cell cell : grid.getCells(Predicates.containsCandidate(digit))) {
            groupedCellSet.add(new GroupedCell(cell));
            groupedCellSet.add(new GroupedCell(grid.getCells(Predicates.inUnit(BOX, cell.getBox())
                    .and(Predicates.inUnit(ROW, cell.getRow()))
                    .and(Predicates.containsCandidate(digit)))));
            groupedCellSet.add(new GroupedCell(grid.getCells(Predicates.inUnit(BOX, cell.getBox())
                    .and(Predicates.inUnit(COL, cell.getCol()))
                    .and(Predicates.containsCandidate(digit)))));
        }
        return groupedCellSet;
    }

    private boolean isConjugatePair(GroupedCell groupedCell1, GroupedCell groupedCell2, int digit) {
        if (groupedCell1.box() == groupedCell2.box()) {
            // Both are in the same box
            return grid.getCells(Predicates.inUnit(BOX, groupedCell1.box())
                    .and(Predicates.containsCandidate(digit))
                    .and(Predicate.not(Predicates.in(groupedCell1.cells()).or(Predicates.in(groupedCell2.cells())))))
                    .length == 0;
        }

        if (groupedCell1.isSingleCell() && groupedCell2.isSingleCell()) {
            // Both are single cells
            return grid.isConjugatePair(groupedCell1.cells()[0], groupedCell2.cells()[0], digit);
        }

        if (!groupedCell1.isSingleCell() && !groupedCell2.isSingleCell()) {
            // Both are grouped cells
            return groupedCell1.unitType() == groupedCell2.unitType() && groupedCell1.unitIndex() == groupedCell2.unitIndex() &&
                    grid.getCells(Predicates.inUnit(groupedCell1.unitType(), groupedCell1.unitIndex())
                            .and(Predicates.containsCandidate(digit))
                            .and(Predicate.not(Predicates.in(groupedCell1.cells()).or(Predicates.in(groupedCell2.cells())))))
                            .length == 0;
        }

        // One is a single cells, the other is a grouped cells
        GroupedCell singleCell = groupedCell1.isSingleCell() ? groupedCell1 : groupedCell2;
        GroupedCell groupedCell = groupedCell1.isSingleCell() ? groupedCell2 : groupedCell1;
        return singleCell.cells()[0].getUnitIndex(groupedCell.unitType()) == groupedCell.unitIndex()
                && grid.getCells(Predicates.inUnit(groupedCell.unitType(), groupedCell.unitIndex())
                .and(Predicates.containsCandidate(digit))
                .and(Predicate.not(Predicates.in(singleCell.cells()).or(Predicates.in(groupedCell.cells())))))
                .length == 0;
    }

    private boolean withGroups(Cycle<Candidate> cycle) {
        return cycle.stream().anyMatch(c -> !c.cells.isSingleCell());
    }

    record Candidate(GroupedCell cells, int digit) {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Candidate(GroupedCell cell1, int digit1))) return false;
            return this.cells.equals(cell1) && this.digit == digit1;
        }

        @Override
        public int hashCode() {
            return this.cells.hashCode() * 31 + this.digit;
        }

        @Override
        public String toString() {
            return String.format("%s(%d)", cells, digit);
        }

        public Cell firstCell() {
            return cells.cells()[0];
        }
    }
}
