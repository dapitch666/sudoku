package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.anne.sudoku.model.UnitType.*;

public class GroupedXCycles extends SolvingTechnique {
    public GroupedXCycles() {
        super("Grouped X-Cycles", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        for (int digit = 1; digit <= 9; digit++) {
            var strongLinks = findGroupedLinks(digit, true);
            var weakLinks = findGroupedLinks(digit, false);
            var cycles = new Graph<>(strongLinks, weakLinks).findAllCycles().stream()
                    .filter(this::hasNoDuplicatedCells) // Skip cycles where the same cell appears multiple times
                    .toList();
            for (Cycle<SuperCell> cycle : cycles) {
                Cycle.CycleType cycleType = cycle.getCycleType(strongLinks);
                // Apply the appropriate rule based on the cycle type
                Rule rule = switch (cycleType) {
                    case CONTINUOUS -> this::rule1;
                    case DISCONTINUOUS_STRONG -> this::rule2;
                    case DISCONTINUOUS_WEAK -> this::rule3;
                };
                var changed = rule.apply(digit, cycle);
                if (!changed.isEmpty()) {
                    incrementCounter();
                    log(0, "%s on %d (length %d) detected in %s%n", cycleType, digit, cycle.size(), cycle);
                    return changed;
                }
            }
        }
        return List.of();
    }

    private boolean hasNoDuplicatedCells(Cycle<SuperCell> cycle) {
        Set<Cell> seenCells = new HashSet<>();
        return cycle.stream()
                .flatMap(superCell -> Arrays.stream(superCell.cells))
                .allMatch(seenCells::add);
    }

    private List<Cell> rule1(int digit, Cycle<SuperCell> cycle) {
        List<Cell> changed = new ArrayList<>();
        Cell[] cycleCells = cycle.stream()
                .flatMap(superCell -> Arrays.stream(superCell.cells))
                .toArray(Cell[]::new);
        for (int i = 0; i < cycle.size() - 1; i += 2) { // Get all weak links
            // // collect all the cells that are in both cycle.get(i) and cycle.get(i + 1)
            Cell[] linkCells = Stream.of(cycle.get(i).cells, cycle.get(i + 1).cells)
                    .flatMap(Arrays::stream)
                    .toArray(Cell[]::new);

            changed.addAll(List.of(grid.getCells(Predicates.containsCandidate(digit)
                    .and(Predicates.in(cycleCells).negate())
                    .and(cell -> Arrays.stream(linkCells).allMatch(cell::isPeer)))));

        }
        if (!changed.isEmpty()) {
            changed.forEach(cell -> cell.removeCandidate(digit));
            log("- Removed candidate {%d} from %s%n", digit, changed);
        }
        return changed;
    }

    private List<Cell> rule2(int digit, Cycle<SuperCell> cycle) {
        // If the adjacent links are links with strong inference,
        // a candidate can be fixed in the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the last one.
        if (!cycle.getLast().isSingleCell()) return List.of();
        Cell cell = cycle.getLast().cells[0];
        var removed = cell.removeAllBut(List.of(digit));
        if (removed.isEmpty()) return List.of();
        log("- Removed candidate(s) %s from %s%n", removed, cell);
        return List.of(cell);
    }

    private List<Cell> rule3(int digit, Cycle<SuperCell> cycle) {
        // If the adjacent links are links with weak inference (broken line),
        // the candidate can be eliminated from the cell at the discontinuity.
        // As the first link in the cycle is considered a weak link,
        // the cell with the discontinuity is the first one.
        if (!cycle.getFirst().isSingleCell()) return List.of();
        Cell cell = cycle.getFirst().cells[0];
        cell.removeCandidate(digit);
        log("- Removed candidate {%d} from %s%n", digit, cell);
        return List.of(cell);
    }

    private Map<SuperCell, List<SuperCell>> findGroupedLinks(int digit, boolean isStrong) {
        Map<SuperCell, List<SuperCell>> links = new HashMap<>();
        Set<SuperCell> superCellSet = getSuperCells(digit);
        for (SuperCell superCell : superCellSet) {
            List<SuperCell> peers = superCellSet.stream()
                    .filter(s -> !s.equals(superCell) && !superCell.intersects(s) && Arrays.stream(s.cells).allMatch(cell -> Arrays.stream(superCell.cells).allMatch(cell::isPeer)))
                    .filter(s -> !isStrong || isConjugatePair(superCell, s, digit))
                    .toList();
            if (!peers.isEmpty()) {
                links.put(superCell, peers);
            }
        }
        return links;
    }

    private boolean isConjugatePair(SuperCell superCell1, SuperCell superCell2, int digit) {
        if (superCell1.box() == superCell2.box()) {
            // Both are in the same box
            return grid.getCells(Predicates.inUnit(BOX, superCell1.box())
                    .and(Predicates.containsCandidate(digit))
                    .and(Predicate.not(Predicates.in(superCell1.cells).or(Predicates.in(superCell2.cells)))))
                    .length == 0;
        }

        if (superCell1.isSingleCell() && superCell2.isSingleCell()) {
            // Both are single cells
            return grid.isConjugatePair(superCell1.cells[0], superCell2.cells[0], digit);
        }

        if (!superCell1.isSingleCell() && !superCell2.isSingleCell()) {
            // Both are grouped cells
            return superCell1.unitType() == superCell2.unitType() && superCell1.unitIndex() == superCell2.unitIndex() &&
                    grid.getCells(Predicates.inUnit(superCell1.unitType(), superCell1.unitIndex())
                            .and(Predicates.containsCandidate(digit))
                            .and(Predicate.not(Predicates.in(superCell1.cells).or(Predicates.in(superCell2.cells)))))
                            .length == 0;
        }

        // One is a single cell, the other is a grouped cell
        SuperCell singleCell = superCell1.isSingleCell() ? superCell1 : superCell2;
        SuperCell groupedCell = superCell1.isSingleCell() ? superCell2 : superCell1;
        return singleCell.cells[0].getUnitIndex(groupedCell.unitType()) == groupedCell.unitIndex()
                && grid.getCells(Predicates.inUnit(groupedCell.unitType(), groupedCell.unitIndex())
                .and(Predicates.containsCandidate(digit))
                .and(Predicate.not(Predicates.in(singleCell.cells).or(Predicates.in(groupedCell.cells)))))
                .length == 0;
    }

    private Set<SuperCell> getSuperCells(int digit) {
        Set<SuperCell> superCellSet = new HashSet<>();
        for (Cell cell : grid.getCells(Predicates.containsCandidate(digit))) {
            superCellSet.add(new SuperCell(cell));
            superCellSet.add(new SuperCell(grid.getCells(Predicates.inUnit(BOX, cell.getBox())
                    .and(Predicates.inUnit(ROW, cell.getRow()))
                    .and(Predicates.containsCandidate(digit)))));
            superCellSet.add(new SuperCell(grid.getCells(Predicates.inUnit(BOX, cell.getBox())
                    .and(Predicates.inUnit(COL, cell.getCol()))
                    .and(Predicates.containsCandidate(digit)))));
        }
        return superCellSet;
    }

    record SuperCell(Cell... cells) {
        UnitType unitType() {
            if (Arrays.stream(cells).map(Cell::getRow).distinct().count() == 1) return ROW;
            if (Arrays.stream(cells).map(Cell::getCol).distinct().count() == 1) return COL;
            throw new IllegalStateException("SuperCell must be in a single row or column");
        }

        int unitIndex() {
            return unitType() == ROW ? cells[0].getRow() : cells[0].getCol();
        }

        int box() {
            return cells[0].getBox();
        }

        boolean isSingleCell() {
            return cells.length == 1;
        }

        boolean intersects(SuperCell other) {
            return Arrays.stream(cells).anyMatch(Arrays.asList(other.cells)::contains);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SuperCell(Cell[] cells1))) return false;
            return new HashSet<>(Arrays.asList(cells)).equals(new HashSet<>(Arrays.asList(cells1)));
        }

        @Override
        public int hashCode() {
            return Arrays.stream(cells).mapToInt(Cell::hashCode).sum();
        }

        @Override
        public String toString() {
            return Arrays.toString(cells);
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(int digit, Cycle<SuperCell> cycle);
    }
}
