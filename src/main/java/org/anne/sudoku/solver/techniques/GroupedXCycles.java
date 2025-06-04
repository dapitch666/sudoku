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
            for (Cycle<GroupedCell> cycle : cycles) {
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
                    log(0, "%s on %d (length %d) detected in %s%n", cycleType, digit, cycle.size(), cycle);
                    return changed;
                }
            }
        }
        return List.of();
    }

    private boolean hasNoDuplicatedCells(Cycle<GroupedCell> cycle) {
        Set<Cell> seenCells = new HashSet<>();
        return cycle.stream()
                .flatMap(groupedCell -> Arrays.stream(groupedCell.cells))
                .allMatch(seenCells::add);
    }

    private List<Cell> rule1(int digit, Cycle<GroupedCell> cycle) {
        List<Cell> changed = new ArrayList<>();
        Cell[] cycleCells = cycle.stream()
                .flatMap(groupedCell -> Arrays.stream(groupedCell.cells))
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

    private List<Cell> rule2(int digit, Cycle<GroupedCell> cycle) {
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

    private List<Cell> rule3(int digit, Cycle<GroupedCell> cycle) {
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

    private Map<GroupedCell, List<GroupedCell>> findGroupedLinks(int digit, boolean isStrong) {
        Map<GroupedCell, List<GroupedCell>> links = new HashMap<>();
        Set<GroupedCell> groupedCellSet = getSuperCells(digit);
        for (GroupedCell groupedCell : groupedCellSet) {
            List<GroupedCell> peers = groupedCellSet.stream()
                    .filter(s -> !s.equals(groupedCell) && !groupedCell.intersects(s) && Arrays.stream(s.cells).allMatch(cell -> Arrays.stream(groupedCell.cells).allMatch(cell::isPeer)))
                    .filter(s -> !isStrong || isConjugatePair(groupedCell, s, digit))
                    .toList();
            if (!peers.isEmpty()) {
                links.put(groupedCell, peers);
            }
        }
        return links;
    }

    private boolean isConjugatePair(GroupedCell groupedCell1, GroupedCell groupedCell2, int digit) {
        if (groupedCell1.box() == groupedCell2.box()) {
            // Both are in the same box
            return grid.getCells(Predicates.inUnit(BOX, groupedCell1.box())
                    .and(Predicates.containsCandidate(digit))
                    .and(Predicate.not(Predicates.in(groupedCell1.cells).or(Predicates.in(groupedCell2.cells)))))
                    .length == 0;
        }

        if (groupedCell1.isSingleCell() && groupedCell2.isSingleCell()) {
            // Both are single cells
            return grid.isConjugatePair(groupedCell1.cells[0], groupedCell2.cells[0], digit);
        }

        if (!groupedCell1.isSingleCell() && !groupedCell2.isSingleCell()) {
            // Both are grouped cells
            return groupedCell1.unitType() == groupedCell2.unitType() && groupedCell1.unitIndex() == groupedCell2.unitIndex() &&
                    grid.getCells(Predicates.inUnit(groupedCell1.unitType(), groupedCell1.unitIndex())
                            .and(Predicates.containsCandidate(digit))
                            .and(Predicate.not(Predicates.in(groupedCell1.cells).or(Predicates.in(groupedCell2.cells)))))
                            .length == 0;
        }

        // One is a single cell, the other is a grouped cell
        GroupedCell singleCell = groupedCell1.isSingleCell() ? groupedCell1 : groupedCell2;
        GroupedCell groupedCell = groupedCell1.isSingleCell() ? groupedCell2 : groupedCell1;
        return singleCell.cells[0].getUnitIndex(groupedCell.unitType()) == groupedCell.unitIndex()
                && grid.getCells(Predicates.inUnit(groupedCell.unitType(), groupedCell.unitIndex())
                .and(Predicates.containsCandidate(digit))
                .and(Predicate.not(Predicates.in(singleCell.cells).or(Predicates.in(groupedCell.cells)))))
                .length == 0;
    }

    private Set<GroupedCell> getSuperCells(int digit) {
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

    record GroupedCell(Cell... cells) {
        UnitType unitType() {
            if (Arrays.stream(cells).map(Cell::getRow).distinct().count() == 1) return ROW;
            if (Arrays.stream(cells).map(Cell::getCol).distinct().count() == 1) return COL;
            throw new IllegalStateException("GroupedCell must be in a single row or column");
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

        boolean intersects(GroupedCell other) {
            return Arrays.stream(cells).anyMatch(Arrays.asList(other.cells)::contains);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GroupedCell(Cell[] cells1))) return false;
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
        List<Cell> apply(int digit, Cycle<GroupedCell> cycle);
    }
}
